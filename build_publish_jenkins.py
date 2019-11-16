# coding=UTF-8
import os
import re
from functools import cmp_to_key
import sys
import getopt
import fnmatch
import shutil
import subprocess
from ftplib import FTP
import zipfile
import time

PGY_KEYS_C = {
    'apiKey': '7e6d99ff26c9940514d937f396b6e996',
    'userKey': '28e13eea4cb2852007b505cfbf8c111d'
}

PGY_KEYS_U = {
    'apiKey': '7e6d99ff26c9940514d937f396b6e996',
    'userKey': '28e13eea4cb2852007b505cfbf8c111d'
}

PGY_KEYS_X = {
    'apiKey': '7e6d99ff26c9940514d937f396b6e996',
    'userKey': '28e13eea4cb2852007b505cfbf8c111d'
}

OUT_PUT_DIR = 'wallet/build/outputs'
global author_name
global output_apk_file_path
global output_mapping_file_path

def start_work(env_type=None, re_publish=False):
    print("current env is <%s>:" % env_type)
    # 1. remove existed output files, e.g apk file and mapping file.
    if not re_publish:
        shutil.rmtree(OUT_PUT_DIR, True)

    # 2. clean && build
    if not re_publish:
        execute('./gradlew clean assembleRelease%s' % env_type.upper())

def _init_params():
    global author_name, output_apk_file_path, output_mapping_file_path
    # author name
    author_name = execute2("git config --global user.email").split('@')[0]

    # apk file to be committed
    files = list_files(OUT_PUT_DIR, "*.apk", recursive=True)
    if len(files) is 0:
        print("Error: apk file not found in ..." + os.path.join(os.getcwd(), OUT_PUT_DIR))
        exit_with_error()
    output_apk_file_path = files

    # mapping file to be committed
    mapping_files = list_files(OUT_PUT_DIR, "mapping.txt", recursive=True)
    output_mapping_file_path = mapping_files

def commit_to_svn(env_type=None):
    _init_params()
    print("committing to svn...")
    global author_name, output_apk_file_path, output_mapping_file_path
    output_apk_name = os.path.basename(output_apk_file_path)

    tmp_dir = os.path.join(os.getcwd(), 'tmp_commit')
    shutil.rmtree(tmp_dir, True)
    app_version_name = output_apk_name.split('_')[2][1:]
    app_bata_number = output_apk_name.split('_')[3]
    app_timestamp = output_apk_name.split('_')[4]
    app_version_code = output_apk_name.split('_')[5]

    # print('%s, %s, %s, %s' % (app_version_name, app_bata_number, app_timestamp, app_version_code))
    if not os.path.exists(tmp_dir):
        os.mkdir('tmp_commit')
    # copy apk file
    shutil.copyfile(output_apk_file_path, os.path.join(tmp_dir, output_apk_name))
    # copy mapping file
    if output_mapping_file_path:
        shutil.copyfile(output_mapping_file_path, os.path.join(tmp_dir, ('mapping_%s.txt' % output_apk_name[:-4])))

    svn_url = 'svn://%s@192.168.1.28/pagoda_qgw/99code/tags/Buy_Android/Buy_android_V%s_%s' % (
        author_name, app_version_name, app_bata_number)
    print("begin to commit to svn: [%s]" % svn_url)

    commit_msg = 'commit revision：%s' % output_apk_name[:-4]
    execute('svn import -m "%s" "%s" "%s"' % (commit_msg, tmp_dir, svn_url))

    print('SVN commit success !!')

    # remove temp directorycurl
    shutil.rmtree(tmp_dir, True)

def upload_to_ftp(env_type=None):
    print("start upload to FTP ...")
    _init_params()
    global author_name, output_apk_file_path, output_mapping_file_path

    output_apk_name = os.path.basename(output_apk_file_path[0])

    tmp_dir = os.path.join(os.getcwd(), 'build/tmp_commit')
    shutil.rmtree(tmp_dir, True)
    app_version_name = output_apk_name.split('_')[2][1:]
    app_bata_number = output_apk_name.split('_')[3]
    app_timestamp = output_apk_name.split('_')[4]
    app_version_code = output_apk_name.split('_')[5]

    # print('%s, %s, %s, %s' % (app_version_name, app_bata_number, app_timestamp, app_version_code))
    if not os.path.exists(tmp_dir):
        os.makedirs('build/tmp_commit')
    # copy apk file
    if output_apk_file_path and len(output_apk_file_path) != 0:
        for apk_file in output_apk_file_path:
            print("copying %s " % apk_file)
            shutil.copyfile(apk_file, os.path.join(tmp_dir, os.path.basename(apk_file)))
    # copy mapping file
    if output_mapping_file_path and len(output_mapping_file_path) != 0:
        # create zip file ONLY for mapping file, it's smaller than the original text file.
        fixed_txt_name = ('mapping_%s.txt' % output_apk_name[:-4])
        fixed_zip_name = ('mapping_%s.zip' % output_apk_name[:-4])
        shutil.copyfile(output_mapping_file_path[0], os.path.join(tmp_dir, fixed_txt_name))
        f = zipfile.ZipFile(os.path.join(tmp_dir, fixed_zip_name), 'w', zipfile.ZIP_DEFLATED)
        f.write(os.path.join(tmp_dir, fixed_txt_name), fixed_txt_name)
        f.close()
        os.remove(os.path.join(tmp_dir, fixed_txt_name))

    print("upload " + tmp_dir)
    xfer = Xfer()
    xfer.setFtpParams('192.168.1.10', 'ftpuser', '123456')
    des_dir = "buy_v%s_%s" % (app_version_name, app_bata_number)
    xfer.upload(tmp_dir, "./android_buy/build/%s" % des_dir)

    print('upload to ftp SUCCESS!! \n'
          'url=ftp://%s@%s/android_buy/build/%s' % ('ftpuser', '192.168.1.10', des_dir))

    # remove temp directory
    shutil.rmtree(tmp_dir, True)


def publish_to_pgy(env_type=None):
    _init_params()
    print("Releasing App to pgyer.com ...")
    global author_name, output_apk_file_path
    if env_type == 'c':
        pgy_key = PGY_KEYS_C
    elif env_type == 'u':
        pgy_key = PGY_KEYS_U
    elif env_type == 'x':
        pgy_key = PGY_KEYS_X
        print('package need to jiagu for production environment')
        # exit_with_error()
    else:
        print("type NOT support， env=<%s>" % env_type )
        exit_with_error()

    output_apk_name = os.path.basename(output_apk_file_path[0])

    whatsnew = '%s' % env_type
    publish_log = '%s_%s_%s' % (author_name, output_apk_name, whatsnew.replace('\n', ' ##'))

    # print(publish_log)
    cmd = 'curl -F "file=@%s" -F "uKey=%s" -F "_api_key=%s" -F "updateDescription=%s" ' \
          'http://www.pgyer.com/apiv1/app/upload' \
          % (output_apk_file_path[0], pgy_key['userKey'], pgy_key['apiKey'], publish_log)

    max_retry = 4
    retry = 0
    upload_success = False
    while retry < max_retry:
        ret = execute2(cmd)
        if ret.find('"code":0') != -1:
            upload_success = True
            print("@@@@@@@ Upload Success !!!!")
            break
        retry = retry + 1
        print("@@@@@@@ Upload failed, retry for [%d] time" % retry)
        time.sleep(3)  # 3 sec
    if not upload_success:
        exit_with_error()
        print("@@@@@@@ Upload failed, fuck the pgyer.com.")


def list_files(path, pattern=None, sort_by_time=True, recursive=False):
    # returns a list of names (with extension, without full path) of all files in folder path
    files = []
    path = path if os.path.isabs(path) else os.path.join(os.getcwd(), path)
    pattern = "*" if pattern is None else pattern
    pathname = os.path.join(path+'\*', pattern)
    # print("ls " + pathname)
    if recursive:
        for root, dirnames, filenames in os.walk(path):
            for filename in fnmatch.filter(filenames, pattern):
                files.append(os.path.join(root, filename))
    else:
        for filename in fnmatch.filter(os.listdir(path), pattern):
            files.append(os.path.join(path, filename))

    files.sort(key=cmp_to_key(_compare_mtime), reverse=sort_by_time)
    # print(files)
    return files


def _compare_mtime(x, y):
    mtime_x = os.stat(x).st_mtime
    mtime_y = os.stat(y).st_mtime
    if mtime_x < mtime_y:
        return -1
    elif mtime_x > mtime_y:
        return 1
    else:
        return 0


def execute(cmd=None):
    print('execute: "' + cmd + '"')
    # return subprocess.call(cmd, shell=True)
    ret = os.system(cmd)
    if ret != 0:
        exit_with_error()
        print('------------- END -------------')


def execute2(cmd=None):
    print('execute2: "' + cmd + '"')
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    out = p.stdout.read().decode('utf-8', 'ignore')
    return out


def exit_with_error():
    sys.exit(-1)

def usage():
    print("usage:" + sys.argv[0] + " -t c")
    print("usage:" + sys.argv[0] + " -t c")
    message = \
        ''' build - upload FTP - release App
        How to use the script:
        -t, --type   the env type you want to build.
                    c : Environmental of test
                    x : Environmental of release
                    jc : Environmental of jenkins test
                    jx : Environmental of jenkins release
        -r, --rePublish  only release, without build & compile
        -h, --help   show this help message
        e.g.
            build_publish.py -t c       # compile & release for ENV_C
            build_publish.py --type c   # compile & release for EVN_C
            build_publish.py -t c -r    # ONLY release for ENV_C, without build & compile
            build_publish.py -h         # show the messages
        '''
    print(message)


########################## FTP Tool .start.################################
_XFER_FILE = 'FILE'
_XFER_DIR = 'DIR'

class Xfer(object):
    '''''
    @note: upload local file or dirs recursively to ftp server
    '''
    def __init__(self):
        self.ftp = None

    def __del__(self):
        pass

    def setFtpParams(self, ip, uname, pwd, port = 21, timeout = 60):
        self.ip = ip
        self.uname = uname
        self.pwd = pwd
        self.port = port
        self.timeout = timeout

    def initEnv(self):
        if self.ftp is None:
            self.ftp = FTP()
            print('### connect ftp server: %s ...' % self.ip)
            self.ftp.connect(self.ip, self.port, self.timeout)
            self.ftp.login(self.uname, self.pwd)
            print(self.ftp.getwelcome())

    def clearEnv(self):
        if self.ftp:
            self.ftp.close()
            print('### disconnect ftp server: %s!'%self.ip)
            self.ftp = None

    def uploadDir(self, localdir='./', remotedir='./'):
        if not os.path.isdir(localdir):
            return
        try:
            self.ftp.cwd(remotedir)
        except:
            """ if remote dir not exists, then we create the dir. """
            self._create_remote_dir(remotedir)
        for file in os.listdir(localdir):
            src = os.path.join(localdir, file)
            if os.path.isfile(src):
                self.uploadFile(src, file)
            elif os.path.isdir(src):
                try:
                    self.ftp.mkd(file)
                except:
                    sys.stderr.write('the dir is exists %s'%file)
                self.uploadDir(src, file)
        self.ftp.cwd('..')

    def _create_remote_dir(self, remote_dir="./"):
        dir_list = remote_dir.split('/')
        for dir_name in dir_list:
            if dir_name == '.':
                continue
            try:
                print("_create_remote_dir " + dir_name)
                self.ftp.cwd(dir_name)
            except:
                """ if remote dir not exists, then we create the dir. """
                self.ftp.mkd(dir_name)
                self.ftp.cwd(dir_name)

    def uploadFile(self, localpath, remotepath='./'):
        if not os.path.isfile(localpath):
            return
        print('+++ upload %s to %s:%s'%(localpath, self.ip, remotepath))
        self.ftp.storbinary('STOR ' + remotepath, open(localpath, 'rb'))

    def __filetype(self, src):
        if os.path.isfile(src):
            index = src.rfind('\\')
            if index == -1:
                index = src.rfind('/')
            return _XFER_FILE, src[index+1:]
        elif os.path.isdir(src):
            return _XFER_DIR, ''

    def upload(self, src, des='./'):
        filetype, filename = self.__filetype(src)

        self.initEnv()
        if filetype == _XFER_DIR:
            self.srcDir = src
            self.uploadDir(self.srcDir, des)
        elif filetype == _XFER_FILE:
            self.uploadFile(src, filename, des)
        self.clearEnv()

########################## FTP Tool .end.################################


def main(args):
    if os.getcwd() != sys.path[0]:
        print("The python script must run on the current directory!")
        exit_with_error()
    try:
        opts, args = getopt.getopt(args, "ht:r", ["help", "type=", "rePublish"])
    except getopt.GetoptError:
        usage()
        exit_with_error()
    if len(opts) == 0 or len(args) != 0:
        usage()
        exit_with_error()
    re_publish = False
    for op, value in opts:
        if op in ('-t', '--type'):
            if value not in ('c', 'x', 'jc', 'jx'):
                usage()
                exit_with_error()
            env_type = value
        elif op in ('-r', '--rePublish'):
            re_publish = True
        elif op in ('-h', '--help'):
            usage()
        else:
            usage()
            exit_with_error()
    start_work(env_type, re_publish)

if __name__ == '__main__':
    main(sys.argv[1:])