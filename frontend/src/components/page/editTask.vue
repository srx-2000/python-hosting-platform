<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-calendar"></i> 任务列表
                </el-breadcrumb-item>
                <el-breadcrumb-item v-if="this.taskData.taskId===undefined">添加任务</el-breadcrumb-item>
                <el-breadcrumb-item v-else>编辑任务</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="form-box">
                <el-form ref="form"
                         :model="form"
                         label-width="100px"
                         label-position="top"
                         class="task-form"
                         :rules="rules"
                >
                    <el-form-item label="任务名称" prop="taskTitle">
                        <el-input v-model="form.taskTitle"></el-input>
                    </el-form-item>
                    <el-form-item label="创建日期"
                                  v-if="isShow()"
                    >
                        <el-col :span="11">
                            <el-date-picker
                                    type="date"
                                    placeholder="选择日期"
                                    v-model="form.date1"
                                    value-format="yyyy-MM-dd"
                                    style="width: 100%;"
                                    disabled
                            ></el-date-picker>
                        </el-col>
                        <el-col class="line" :span="2">-</el-col>
                        <el-col :span="11">
                            <el-time-picker
                                    placeholder="选择时间"
                                    v-model="form.date2"
                                    style="width: 100%;"
                                    disabled
                                    value-format="HH:mm:ss"
                            ></el-time-picker>
                        </el-col>
                    </el-form-item>
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="gpu" prop="gpu">
                                <el-cascader :options="options"
                                             v-model="form.gpu">
                                </el-cascader>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="运行环境" prop="env.envName">
                                <el-input v-model="form.env.envName"
                                :disabled="isEdit()"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item label="最大运行时长(单位秒)" prop="deadTime">
                        <el-input v-model="form.deadTime"></el-input>
                    </el-form-item>
                    <el-form-item label="导入requirements文件">
                        <el-button type="primary" size="mini" @click="visable">导入</el-button>
                    </el-form-item>
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="上传py文件">
                                <el-upload
                                        action=""
                                        multiple
                                        :file-list="this.pyFiles"
                                        :auto-upload="false"
                                        :on-change="fileChange"
                                        :on-remove="handleremove"
                                        accept=".py"
                                >
                                    <i class="el-icon-upload"></i>
                                    <div class="el-upload__text">将文件拖到此处,或<em>点击上传</em></div>
                                    <div class="el-upload__tip" slot="tip">只能上传.py文件，且不超过1MB(可多选)</div>
                                </el-upload>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="上传input文件">
                                <el-upload
                                        action=""
                                        multiple
                                        :file-list="this.inputFiles"
                                        :auto-upload="false"
                                        :on-change="fileChange1"
                                        :on-remove="handleremove1"
                                >
                                    <i class="el-icon-upload"></i>
                                    <div class="el-upload__text">将文件拖到此处,或<em>点击上传</em></div>
                                    <div class="el-upload__tip" slot="tip">文件大小不超过100MB(可多选，多选大小总和不超过100MB)</div>
                                </el-upload>
                            </el-form-item>
                        </el-col>
                    </el-row>


                    <el-form-item label="任务描述">
                        <el-input type="textarea" rows="5" v-model="form.taskDescription"></el-input>
                    </el-form-item>
                    <div class="button-center">
                        <el-button type="primary" @click="upload('form')">提交</el-button>
                        <el-button @click="back">取消</el-button>
                    </div>
                </el-form>
            </div>
        </div>

        <el-dialog title="上传requirements" :visible.sync="editVisible">
            <!--            <el-form ref="form" :model="form" label-width="70px">-->
            <el-row>
                <el-col :span="22">
                    <el-upload
                            ref="uploads"
                            action=""
                            :file-list="requirementsFile"
                            accept=".txt"
                            :auto-upload="false"
                            :on-change="fileChange2"
                            :on-remove="handleremove2"
                            list-type="picture"
                    >
                        <el-button slot="trigger" size="small" type="primary">点击上传</el-button>
                        <div slot="tip" class="el-upload_tip" style="margin-top: 10px">只能上传.txt文件,且不超过1M</div>
                    </el-upload>
                </el-col>
            </el-row>
            <span slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveEdit">确 定</el-button>
                <el-button @click="cancelEdit">取 消</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import service from "../../utils/request";
    import storageUtil from "../../utils/storageUtil";

    export default {
        name: 'baseform',
        data() {
            var envNameReg = /[^/\\:*?<>|"@]/;
            var deadTimeReg = /^[1-9]\d*/;
            var validateEnvName = (rule, value, callback) => {
                if (!envNameReg.test(value)) {
                    callback(new Error("输入的环境名包含非法字符"))
                } else {
                    this.axios.get("/isEnvExist?envName=" + this.form.env.envName + "&userKey=" + this.form.userKey).then((response => {
                        if (response.data.code !== 434) {
                            callback(new Error("环境不存在"))
                        } else {
                            callback()
                        }
                    }))
                }
            };
            var validateDeadTime = (rule, value, callback) => {
                if (!deadTimeReg.test(value)) {
                    callback(new Error("请输入正整数"))
                } else {
                    callback()
                }
            };
            return {
                editVisible: false,
                requirementsFile: [],
                pyFiles: [],
                inputFiles: [],
                taskData: {},
                options: [],
                form: {
                    env: {
                        envName: ""
                    },
                    taskKey: "",
                    userKey: localStorage.getItem("ms_userKey"),
                    taskTitle: '',
                    deadTime: 3600,
                    taskDescription: "",
                    date1: '',
                    date2: '',
                    createTime: "",
                    gpu: [],
                    gpuId: ""
                },
                rules: {
                    'env.envName': [
                        {required: true, message: '请输入环境名', trigger: 'blur'},
                        {validator: validateEnvName, trigger: "blur"},
                    ],
                    deadTime: [
                        {required: true, message: '请输入结束时间', trigger: 'blur'},
                        {validator: validateDeadTime, trigger: "blur"},
                    ],
                    gpu: [
                        {required: true, message: '请选择使用的gpu', trigger: 'blur'},
                    ],
                    taskTitle: [
                        {required: true, message: '请输入任务名称', trigger: 'blur'},
                    ],
                }
            };
        },
        beforeRouteEnter(to, from, next) {
            var taskKey = to.params.id;
            if (to.params.id === undefined && !localStorage.getItem("ms_taskKey") && to.fullPath === "/editTask") {
                next("/task")
            } else if (to.fullPath === "/addTask") {
                service.get("/getGpuList").then(response => {
                    next(vm => {
                        if (vm.options.length === 0) {
                            vm.getData(response);
                        }
                        vm.setData(null)
                    })
                })
            } else {
                if (to.params.id === undefined) {
                    taskKey = localStorage.getItem("ms_taskKey");
                } else {
                    localStorage.setItem("ms_taskKey", taskKey)
                }
                service.get("getTaskByTaskKey?taskKey=" + taskKey).then(response1 => {
                    console.log(response1.code)
                    if (response1.code == 207) {
                        if (response1.result.status != 1) {
                            next("/task")
                        }
                        service.get("/getGpuList").then(response => {
                            next(vm => {
                                if (vm.options.length === 0) {
                                    vm.getData(response);
                                }
                                vm.setData(response1.result)
                            });
                        })
                    }
                })
            }
        },
        methods: {
            cancelEdit() {
                this.editVisible = false;
                // this.requirementsFile=[]
            },
            visable() {
                this.editVisible = true;
            },
            saveEdit() {
                this.editVisible = false;
            },
            handleremove(file) {
                var number = this.pyFiles.indexOf(file);
                this.pyFiles.splice(number, 1);
            },
            handleremove1(file) {
                var number = this.inputFiles.indexOf(file);
                this.inputFiles.splice(number, 1);
            },
            handleremove2(file, fileList) {
                var number = this.requirementsFile.indexOf(file);
                this.requirementsFile.splice(number, 1);
                // this.inputFiles.splice(number, 1);
            },
            fileChange(file) {
                let existsFileFlag = false;
                for (let i = 0; i < this.pyFiles.length; i++) {
                    if ((file.name === this.pyFiles[i].name) && (file.size === this.pyFiles[i].size)) {
                        // 如果存在了，赋值true
                        existsFileFlag = true;
                    }
                }
                this.pyFiles.push(file.raw);
                if (existsFileFlag) {
                    // 否则不加入
                    this.pyFiles.pop();
                    this.$message.error("有文件已加入")
                }
            },
            fileChange1(file) {
                let existsFileFlag = false;
                for (let i = 0; i < this.inputFiles.length; i++) {
                    if ((file.name === this.inputFiles[i].name) && (file.size === this.inputFiles[i].size)) {
                        // 如果存在了，赋值true
                        existsFileFlag = true;
                    }
                }
                this.inputFiles.push(file.raw);
                if (existsFileFlag) {
                    // 否则不加入
                    this.inputFiles.pop();
                    this.$message.error("有文件已加入")
                }
            },
            fileChange2(file) {
                let existsFileFlag = false;
                for (let i = 0; i < this.requirementsFile.length; i++) {
                    if ((file.name === this.requirementsFile[i].name) && (file.size === this.requirementsFile[i].size)) {
                        // 如果存在了，赋值true
                        existsFileFlag = true;
                    }
                }
                this.requirementsFile.push(file.raw);
                if (existsFileFlag) {
                    // 否则不加入
                    this.requirementsFile.pop();
                    this.$message.error("有文件已加入")
                }
                if (this.requirementsFile.length > 1) {
                    this.requirementsFile.pop();
                    this.$message.error("只可以上传一个requirements文件呦")
                }
            },
            upload(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.form.gpuId = this.form.gpu[1];
                        var path = this.$route.path;
                        if (path.includes("addTask")) {
                            this.form.createTime = undefined;
                            this.form.taskKey = undefined;
                        } else if (path.includes("editTask")) {
                            var taskKey = localStorage.getItem("ms_taskKey");
                            if (taskKey !== "") {
                                this.form.taskKey = taskKey
                                this.form.createTime = this.form.date1 + " " + this.form.date2
                                console.log(this.form.createTime)
                            } else {
                                this.$message.error("出现莫名错误！")
                            }
                        }
                        let formData = new FormData();
                        for (var i = 0; i < this.pyFiles.length; i++) {
                            formData.append("pyFiles", this.pyFiles[i]);
                        }
                        for (var i = 0; i < this.inputFiles.length; i++) {
                            formData.append("inputFiles", this.inputFiles[i])
                        }
                        formData.append("requirementsFile", this.requirementsFile[0]);
                        formData.append("task", JSON.stringify(this.form));
                        let config = {
                            'Content-Type': 'multipart/form-data',
                            showLoading: true
                        };
                        if (path.includes("addTask")) {
                            service.post("/insertTask", formData, config).then(response => {
                                if (response.code === 427) {
                                    this.$message.error(response.message)
                                } else {
                                    this.$router.push("/task");
                                    if (response.code === 426) {
                                        this.$message.error(response.message)
                                    } else if (response.code === 206) {
                                        this.$message.success('提交成功！');
                                    }
                                }
                            })
                        } else if (path.includes("editTask")) {
                            service.post("/updateTask", formData, config).then(response => {
                                if (response.code === 427) {
                                    this.$message.error(response.message)
                                } else {
                                    this.$router.push("/task");
                                    if (response.code === 430) {
                                        this.$message.error(response.message + "-报错详情见log")
                                    } else if (response.code === 208) {
                                        this.$message.success('提交成功！');
                                    }
                                }
                            })
                        }
                    }
                })
            },
            back() {
                this.$router.push('/task');
            },
            isShow() {
                if (this.$route.path === "/addTask")
                    return false;
                else
                    return true;
            },
            isEdit(){
                if (this.$route.path === "/editTask")
                    return true;
                else
                    return false;
            },

            setData(res) {
                if (res == null) {
                    this.requirementsFile = [];
                    this.pyFiles = [];
                    this.inputFiles = [];
                    this.taskData = {};
                    this.form.date1 = "";
                    this.form.date2 = "";
                    this.form.taskTitle = "";
                    this.form.env.envName = "";
                    this.form.deadTime = 3600;
                    this.form.taskDescription = "";
                    // this.form.gpu=[];
                } else {
                    this.taskData = res;
                    this.form.taskKey = localStorage.getItem("ms_taskKey");
                    this.form.date1 = this.taskData.createTime.split(" ")[0];
                    this.form.date2 = this.taskData.createTime.split(" ")[1];
                    this.form.taskTitle = this.taskData.taskTitle;
                    this.form.env.envName = this.taskData.env.envName.split("@@")[0];
                    this.form.deadTime = this.taskData.deadTime;
                    this.form.taskDescription = this.taskData.taskDescription;
                    this.form.gpu[1] = Number(this.taskData.gpuId);
                    function f(a, x) {
                        for (var i = 0; i < a.length; i++) {
                            for (var j in a[i].children) {
                                if (a[i].children[j].value == x) {
                                    return a[i].label
                                }
                            }
                        }
                    }

                    this.form.gpu[0] = f(this.options, this.form.gpu[1]);
                }

            },
            getData(result) {
                if (result.code == 207) {
                    let res = result.result.gpuList;
                    res.forEach(element => {
                        const isContain = this.options.some(item => {
                            if (item.label === element.name) {
                                return true;
                            } else return false;
                        });
                        if (!isContain) {
                            this.options.push({
                                label: element.name,
                                value: element.name,
                                children: [{label: "gpu-" + element.number, value: element.number}]
                            });
                        } else {
                            var t = this.options.find(item => item.label === element.name);
                            t.children.push({
                                label: "gpu-" + element.number,
                                value: element.number
                            });
                        }
                    })
                } else {
                    this.$message("gpu列表获取失败")
                }
            },
        }
    };
</script>
<style>
    .button-center {
        text-align: center;
    }

    .task-form .el-form-item__label {
        font-weight: bold;
    }
</style>
