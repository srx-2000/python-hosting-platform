<template>
    <div class="register-wrap">
        <div class="ms-register">
            <div class="ms-title">
<!--                <el-aside width="45px" class="ml5">-->
                    <el-button @click="goBack" icon="el-icon-arrow-left" type="text" size="large" class="ml5"></el-button>
<!--                </el-aside>-->
<!--                <el-main>-->
                    <span>注册</span>
<!--                </el-main>-->
            </div>
            <!--            <el-page-header @back="goBack" content="注册" title="" class="ms-title">-->
            <!--            </el-page-header>-->
            <el-form ref="reigsterForm"
                     :rules="rules"
                     :label-position="labelPosition"
                     label-width="100px"
                     :model="reigsterForm"
                     class="ms-content"
                     size="middle">
                <el-form-item label="昵称：" prop="nickname">
                    <el-input v-model="reigsterForm.nickname" placeholder="昵称"></el-input>
                </el-form-item>
                <el-form-item label="用户名：" prop="username">
                    <el-input v-model="reigsterForm.username" placeholder="用户名"></el-input>
                </el-form-item>
                <el-form-item label="邮箱：" prop="email">
                    <el-input v-model="reigsterForm.email" placeholder="邮箱"></el-input>
                </el-form-item>
                <el-form-item label="手机：" prop="phone">
                    <el-input v-model="reigsterForm.phone" placeholder="手机号"></el-input>
                </el-form-item>
                <el-form-item label="密码：" prop="password">
                    <el-input v-model="reigsterForm.password" type="password" placeholder="密码"></el-input>
                </el-form-item>
                <el-form-item label="确认密码：" prop="confirmPassword">
                    <el-input v-model="reigsterForm.confirmPassword" type="password"
                              @keyup.enter.native="submitForm()" placeholder="确认密码"></el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button size="large" type="primary" @click="submitForm('reigsterForm')">注册</el-button>
                    <el-button size="large" @click="resetForm('reigsterForm')">重置</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
    export default {
        data() {
            let passwordReg = /(?!^(\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\w~!@#$%^&*?]{6,12}$/
            let emailReg = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
            let phoneReg = /0?(13|14|15|17|18|19)[0-9]{9}/
            var validateNewPwd = (rule, value, callback) => {
                if (!passwordReg.test(value)) {
                    callback(new Error('密码应是6-12位数字、字母或字符'))
                } else {
                    callback()
                }
            }
            var validatePhone = (rule, value, callback) => {
                if (!phoneReg.test(value)) {
                    callback(new Error('请输入有效的手机号'))
                } else {
                    callback()
                }
            }
            var validateUsername = (rule, value, callback) => {
                this.axios.get("/isUsernameExist?username=" + value).then((response => {
                    if (response.data.code === 437) {
                        callback(new Error(response.data.message))
                    } else {
                        callback()
                    }
                }))
            }
            var validateEmail = (rule, value, callback) => {
                if (!emailReg.test(value)) {
                    callback(new Error("请输入有效的邮箱"))
                } else {
                    this.axios.get("/isEmailExist?email=" + value).then((response => {
                        if (response.data.code === 438) {
                            callback(new Error(response.data.message))
                        } else {
                            callback()
                        }
                    }))
                }

            }
            var validateComfirmPwd = (rule, value, callback) => {
                if (!passwordReg.test(value)) {
                    callback(new Error('密码应是6-12位数字、字母或字符'))
                } else if (this.reigsterForm.password !== value) {
                    callback(new Error('确认密码与新密码不一致'))
                } else {
                    callback()
                }
            }
            return {
                labelPosition: 'right',
                reigsterForm: {
                    username: "",
                    password: "",
                    nickname: "",
                    phone: "",
                    email: "",
                    confirmPassword: ""
                },
                rules: {
                    nickname: [{required: true, message: '请输入昵称', trigger: 'blur'}],
                    username: [
                        {required: true, message: '请输入用户名', trigger: 'blur'},
                        {validator: validateUsername, trigger: "blur"}
                    ],
                    password: [
                        {required: true, message: '请输入密码', trigger: 'blur'},
                        {validator: validateNewPwd, trigger: "blur"}
                    ],
                    phone: [
                        {required: true, message: '请输入手机号码', trigger: 'blur'},
                        {validator: validatePhone, trigger: "blur"}
                    ],
                    email: [
                        {required: true, message: '请输入邮箱', trigger: 'blur'},
                        {validator: validateEmail, trigger: "blur"}],
                    confirmPassword: [
                        {required: true, message: '请输入确认密码', trigger: 'blur'},
                        {validator: validateComfirmPwd, trigger: 'blur'}
                    ]
                },
            };
        },
        methods: {
            resetForm(formName) {
                this.$nextTick(() => {
                    this.$refs[formName].resetFields();
                });

            },
            goBack() {
                this.$router.push('/login');
            },
            submitForm(formName) {
                console.log(this.reigsterForm.username)
                console.log(this.reigsterForm.password)
                this.$refs[formName].validate((valid) => {  //开启校验
                    if (valid) {   // 如果校验通过，请求接口，允许提交表单
                        alert('submit!');
                    } else {   //校验不通过
                        return false;
                    }
                });
                this.axios.post("/register", {
                        username: this.reigsterForm.username,
                        password: this.reigsterForm.password,
                        nickname: this.reigsterForm.nickname,
                        phone: this.reigsterForm.phone,
                        email: this.reigsterForm.email
                    }
                ).then((response) => {
                    console.log(response)
                    if (response.data.code == 216) {
                        this.$router.push('/login');
                    } else {
                        this.$message.error('注册失败，请检查网络或填写是否正确');
                        console.log('error submit!!');
                    }
                    console.log(response.data)
                }).catch((response) => {
                    console.log(response)
                })
            },
        }
    }
</script>

<style scoped>
    .register-wrap {
        width: 100%;
        height: 100%;
        /*background-image: url(../../assets/img/login-bg.jpg);*/
        background-color: #F4F6F9;
        display: flex;
    }

    .ms-register {
        width: 450px;
        border-radius: 5px;
        margin: auto;
        background: rgba(255, 255, 255, 0.7);
        box-shadow: 0 0 2px 4px rgba(128, 128, 128, 0.1);
        /*overflow: hidden;*/
    }

    .ms-title {
        width: 100%;
        text-align: center;
        line-height: 50px;
        font-size: 20px;
        color: #6777ef;
        border-bottom: 1px solid #ddd;
        font-weight:bold
    }
    .login-btn {
        text-align: center;
    }

    .ml5 {
        margin-left: 10px;
        font-weight:bold;
        vertical-align: middle;
        float: left;
    }

    .ms-content {
        padding: 30px 30px;
    }


</style>
