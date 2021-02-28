<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-calendar"></i> 任务列表
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <el-tabs v-model="message">
                <el-tab-pane :label="`待开始任务(${task.todoCount})`" name="first">
                    <el-button type="primary" icon="el-icon-plus" class="mgb20" @click="addTask">添加任务</el-button>
                    <el-table :data="task.todoTaskList" :show-header="true" style="width: 100%">
                        <el-table-column
                                label="序号"
                                width="100"
                                prop="index"
                                type="index"
                                :index="get_index"
                                align="center">
                        </el-table-column>
                        <el-table-column
                                prop="taskTitle"
                                label="任务名称">
                        </el-table-column>
                        <el-table-column
                                prop="taskKey"
                                width="290"
                                label="任务秘钥">
                        </el-table-column>
                        <el-table-column
                                prop="createTime"
                                width="200"
                                label="创建日期"></el-table-column>
                        <el-table-column width="310" label="操作">
                            <template slot-scope="scope">
                                <el-row>
                                    <el-button size="small" @click="editTask(scope.$index)" icon="el-icon-edit">编辑
                                    </el-button>
                                    <el-button size="small" @click="getpath(scope.$index)" icon="el-icon-s-flag"
                                               type="success">获取路径
                                    </el-button>
                                    <el-button size="small" @click="addqueue(scope.$index)" type="primary"
                                               icon="el-icon-position">开始运行
                                    </el-button>
                                </el-row>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
                <el-tab-pane :label="`已插入队列任务(${task.doingCount})`" name="second">
                    <el-table :data="task.doingTaskList" :show-header="true" style="width: 100%">
                        <el-table-column
                                label="序号"
                                width="%10"
                                prop="index"
                                type="index"
                                :index="get_index"
                                align="center">
                        </el-table-column>
                        <el-table-column
                                prop="taskTitle"
                                label="任务名称">
                        </el-table-column>
                        <el-table-column
                                prop="taskKey"
                                width="260"
                                label="任务秘钥">
                        </el-table-column>
                        <el-table-column
                                prop="createTime"
                                width="180"
                                label="创建日期"></el-table-column>
                        <el-table-column width="240" label="操作">
                            正在运行中或已插入队列
                            <el-tooltip class="item" effect="dark" content='请耐心等待完成....' placement="top-start">
                                <i class="el-icon-question" style="color: rgba(0, 0, 0, 0.5)"></i>
                            </el-tooltip>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
                <el-tab-pane :label="`已完成任务(${task.complementCount})`" name="third">
                    <el-table :data="task.complementTaskList" :show-header="true" style="width: 100%">
                        <el-table-column
                                label="序号"
                                width="%10"
                                prop="index"
                                type="index"
                                :index="get_index"
                                align="center">
                        </el-table-column>
                        <el-table-column
                                prop="taskTitle"
                                label="任务名称">
                        </el-table-column>
                        <el-table-column
                                prop="taskKey"
                                width="260"
                                label="任务秘钥">
                        </el-table-column>
                        <el-table-column
                                prop="createTime"
                                width="180"
                                label="创建日期"></el-table-column>
                        <el-table-column width="240" label="操作">
                            已完成
                            <el-tooltip class="item" effect="dark" content='请使用任务秘钥以及用户秘钥获取结果' placement="top-start">
                                <i class="el-icon-question" style="color: rgba(0, 0, 0, 0.5)"></i>
                            </el-tooltip>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
        </div>
        <el-dialog title="文件绝对路径" :visible.sync="this.visible"
                   :before-close="back">
            <p>输出文件绝对路径：
                <el-button icon="el-icon-document-copy" ref="copy" round type="primary"
                           :data-clipboard-text="this.outputPath" @click="copy"
                           id="copy_text">复制
                </el-button>
                <br/><br/>{{this.outputPath}}
            </p>

            <br/>
            <p>输入文件绝对路径：
                <el-button icon="el-icon-document-copy" ref="copy" round type="primary"
                           :data-clipboard-text="this.inputPath" @click="copy"
                           id="copy_text">复制
                </el-button>
                <br/><br/>{{this.inputPath}}
            </p>

            <br/>
            <p>py文件绝对路径：
                <el-button icon="el-icon-document-copy" ref="copy" round type="primary"
                           :data-clipboard-text="this.pyPath" @click="copy"
                           id="copy_text">复制
                </el-button>
                <br/><br/>{{this.pyPath}}
            </p>

            <br/>
            <!--            <span slot="footer" class="dialog-footer">-->
            <!--                <el-button type="primary" @click="back">确 定</el-button>-->
            <!--                <el-button @click="back">取 消</el-button>-->
            <!--            </span>-->
        </el-dialog>
    </div>
</template>

<script>
    import service from "../../utils/request";
    import storageUtil from "../../utils/storageUtil";
    import Clipboard from 'clipboard';

    export default {
        name: "",
        data() {
            return {
                outputPath: "",
                inputPath: "",
                pyPath: "",
                visible: false,
                message: 'first',
                task: {
                    todoTaskList: [],
                    todoCount: 0,
                    complementTaskList: [],
                    complementCount: 0,
                    allTaskList: [],
                    allCount: 0,
                    doingTaskList: [],
                    doingCount: 0,
                },
                query: {
                    currentPage: 1,
                    pageSize: 10000,
                },
                userKey: localStorage.getItem("ms_userKey")
            }
        },
        mounted() {
            this.getData()
        },
        methods: {
            copy: function () {
                var _this = this;
                var clipboard = new Clipboard('#copy_text');

                clipboard.on('success', e => {
                    alert("dd")
                    // 释放内存
                    clipboard.destroy()
                })
                clipboard.on('error', e => {
// 不支持复制
                    Message({
                        message: '该浏览器不支持自动复制',
                        type: 'warning'
                    });
// 释放内存
                    clipboard.destroy()
                })
            },
            back() {
                this.visible = false;
            },
            addTask() {
                this.$router.push("/addTask")
            },
            editTask(index) {
                var taskKey = this.task.todoTaskList[index].taskKey;
                this.$router.push({name: 'editTask', params: {id: encodeURIComponent(taskKey)}})
            },
            get_index(index) {
                return (this.query.currentPage - 1) * this.query.pageSize + index + 1;
            },
            getData() {
                this.task.doingCount = 0;
                this.task.doingTaskList = [];
                service.get("/getTaskListByUserKey?userKey=" + this.userKey + "&status=1&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize).then(response => {
                    if (response.code === 207) {
                        this.task.todoTaskList = response.result.taskList;
                        this.task.todoCount = response.result.totalCount;
                    }
                });
                service.get("/getTaskListByUserKey?userKey=" + this.userKey + "&status=0&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize).then(response => {
                    if (response.code === 207) {
                        this.task.doingTaskList.push.apply(this.task.doingTaskList, response.result.taskList);
                        this.task.doingCount += response.result.totalCount;
                    }
                });
                service.get("/getTaskListByUserKey?userKey=" + this.userKey + "&status=2&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize).then(response => {
                    if (response.code === 207) {
                        this.task.doingTaskList.push.apply(this.task.doingTaskList, response.result.taskList);
                        this.task.doingCount += response.result.totalCount;
                    }
                });
                service.get("/getTaskListByUserKey?userKey=" + this.userKey + "&status=-1&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize).then(response => {
                    if (response.code === 207) {
                        this.task.complementTaskList = response.result.taskList;
                        this.task.complementCount = response.result.totalCount;
                    }
                })
            },
            getpath(index) {
                var requirementsPath = this.task.todoTaskList[index].requirementsPath;
                var basePath = requirementsPath.replace("requirements.txt", "");
                this.outputPath = basePath + "output/"
                this.inputPath = basePath + "input/"
                this.pyPath = basePath + "py/"
                this.visible = true;
            },
            addqueue(index) {
                var taskKey = this.task.todoTaskList[index].taskKey;
                var gpuId = this.task.todoTaskList[index].gpuId;
                this.axios.get("/insertQueue?taskKey=" + taskKey + "&gpuId=" + gpuId).then(response => {
                    if (response.data.code === 209) {
                        this.$message.success(response.data.message);
                        // this.task.doingTaskList.push(this.task.todoTaskList[index]);
                        // this.task.todoTaskList.splice(index, 1)
                        this.getData()
                    } else {
                        this.$message.error("任务插入队列失败")
                    }
                })

            },
        }
    }
</script>

<style scoped>
    .message-title {
        cursor: pointer;
    }

    .mgb20 {
        margin-bottom: 20px;
    }
</style>
