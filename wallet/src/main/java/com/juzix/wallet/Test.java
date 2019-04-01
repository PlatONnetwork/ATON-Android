package com.juzix.wallet;
import com.alibaba.fastjson.annotation.JSONField;
/**
 * @author matrixelement
 */
public class Test {

    public static void main(String[] args) {

//        Person person = new Person();
//        person.name = "小明";
//        Student student = new Student();
//        student.gradle = 10;
//        student.nickName = "haha";
//        person.student = student;
//
//        String json = JSONUtil.toJSONString(person);
//
//        System.out.println(json);
//
//        Person person1 = JSONUtil.parseObject("{\"name\":\"小明\",\"stu\":{\"gradle\":10,\"nickName\":\"haha\"}}",Person.class);
//
//        System.out.println(person1.toString());

    }

    static class Person {

        private String name;

        @JSONField(name = "stu")
        private Student student;

        public Person() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", student=" + student +
                    '}';
        }
    }

    static class Student {

        private int gradle;

        private String nickName;

        public Student() {
        }

        public int getGradle() {
            return gradle;
        }

        public void setGradle(int gradle) {
            this.gradle = gradle;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "gradle=" + gradle +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }
    }

}
