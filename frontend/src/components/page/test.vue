<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-folder-opened"></i> 结果下载
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <el-form ref="form" label-width="80px"
                     style="width: 50%"
                     :rules="rules"
                     :model="form">
                <el-form-item label="任务秘钥" prop="taskKey">
                    <el-input v-model="form.taskKey" placeholder="任务秘钥"></el-input>
                </el-form-item>
                <div class="button-center">
                    <el-button type="primary" @click="submit('form')">提交</el-button>
                    <el-button @click="reset">重置</el-button>
                    <a v-if="visible" :href="'http://localhost:8888/download?taskKey='+this.form.taskKey" class="mr10">点击下载</a>
                </div>
                <!--                <el-button size="small" type="primary" @click="submit('form')">提取</el-button>-->
            </el-form>
        </div>
    </div>
</template>

<script>
    import "echarts/theme/macarons";

    export default {
        name: 'download',
        data() {
            var validateTaskKey = (rule, value, callback) => {
                this.axios.get("/isTaskExist?userKey=" + this.userKey + "&taskKey=" + this.form.taskKey).then((response => {
                    console.log(response)
                    if (response.data.code === 439) {
                        callback(new Error("该任务不存在"))
                    } else {
                        callback()
                    }
                }))
            };
            return {
                visible:false,
                form: {
                    taskKey: "",
                },
                userKey: localStorage.getItem("ms_userKey"),
                rules: {
                    taskKey: [
                        {required: true, message: '请输入秘钥', trigger: 'blur'},
                        {validator: validateTaskKey, trigger: "blur"}
                    ]
                }
            }
        },
        mounted() {
        },
        methods: {
            click(){
                return "/download?taskKey="+this.form.taskKey
            },
            reset() {
                this.form.taskKey = ""
            },
            submit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.axios.get("/download?taskKey=" + this.form.taskKey).then(() => {
                            this.visible=true
                            this.$message.warning("请点击旁边的连接下载")
                        })
                    } else {   //校验不通过
                        return false;
                    }
                });

            }
        }

    }
</script>

<style scoped>
    .button-center {
        text-align: center;
    }

    .mr10 {
        margin-left: 10px;
    }

</style>
