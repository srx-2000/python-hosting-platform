<template>
    <div class="login-wrap">
        <div class="ms-login">
            <h4 class="ms-title">登录</h4>
            <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="param.username" placeholder="username">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input
                            type="password"
                            placeholder="password"
                            v-model="param.password"
                            @keyup.enter.native="submitForm()"
                    >
                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm()">登录</el-button>
                </div>
                <router-link to="/register" class="login-tips">还没有账号？点击注册</router-link>
            </el-form>
        </div>
    </div>
</template>

<script>
    import service from "../../utils/request";

    export default {
        data: function () {
            return {
                param: {
                    username: 'srx',
                    password: 'srx62600',
                },
                rules: {
                    username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
                    password: [{required: true, message: '请输入密码', trigger: 'blur'}],
                },
            };
        },
        methods: {
            submitForm() {
                if (this.param.password !== "" && this.param.username !== "") {
                    service.post("/login", {
                            username: this.param.username,
                            password: this.param.password
                        }
                    ).then((response) => {
                        // console.log(response)
                        if (response.code === 204) {
                            this.$message.success('登录成功');
                            localStorage.setItem('ms_username', response.result.username);
                            localStorage.setItem('ms_email', response.result.email);
                            localStorage.setItem('ms_nickname', response.result.nickname);
                            localStorage.setItem('ms_userKey', response.result.userKey);
                            this.$router.push('/');
                        } else {
                            this.$message.error('账号密码错误');
                            console.log('error submit!!');
                            // return false;
                        }
                        // console.log(response.data)
                    }).catch((response) => {
                        console.log(response)
                    })
                } else {
                    this.$message.error("请输入账号密码");
                }
            },
        },
    };
</script>

<style scoped>
    .login-wrap {
        position: relative;
        width: 100%;
        height: 100%;
        background-image: url(../../assets/img/login-bg.jpg);
        background-color: #F4F6F9;
        background-size: 100%;
    }

    .ms-title {
        width: 100%;
        line-height: 50px;
        text-align: center;
        font-size: 20px;
        color: #6777ef;
        font-size: 16px;
        border-bottom: 1px solid #ddd;
    }

    .ms-login {
        position: absolute;
        left: 80%;
        top: 50%;
        width: 350px;
        margin: -190px 0 0 -175px;
        border-radius: 5px;
        background: rgba(255, 255, 255, 0.7);
        box-shadow: 0 0 2px 4px rgba(128, 128, 128, 0.2);
        overflow: hidden;
    }

    .ms-content {
        padding: 40px 40px;
    }

    .login-btn {
        text-align: center;
    }

    .login-btn button {
        width: 100%;
        height: 36px;
        margin-bottom: 10px;
    }

    .login-tips {
        font-size: 14px;
        line-height: 30px;
        text-underline: blue;
        text-align: right;
        color: #3D9FFC;
    }
</style>
