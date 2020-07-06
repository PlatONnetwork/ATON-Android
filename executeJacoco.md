#### jacoco覆盖率统计操作说明

    单测试，在debug模式下，在手机上运行一下项目，就会在手机目录生成覆盖率的文件。
* 在BaseActivity的onDestory中定义了生成coverage.ec文件，输出目录是在Android设备的/data/data/com.platon.aton/files 目录下
* 取出coverage.ec，复制到wallet/build/outputs/code-coverage/connected/目录下，如果无此目录，则可以手动创建（目录必须一致）。
   然后执行gradlew jacocoTestReport命令
   会在wallet\build\reports\jacoco\jacocoTestReport目录下，正常报告
* 生成覆盖率报告后，则可以执行gradlew sonarqube命令，改命令在根目录下的gralde.properties下有详细说明，上传至sonar后台进行查看