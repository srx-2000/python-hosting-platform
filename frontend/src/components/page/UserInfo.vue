<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-user-solid"></i> 用户信息
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <el-row :gutter="20">
            <el-col :span="8">
                <el-card shadow="hover" class="mgb20" style="height:252px;">
                    <div class="user-info">
                        <img src="../../assets/img/img.jpg" class="user-avator" alt/>
                        <!--                        <p>您好！</p>-->
                        <div class="user-info-cont">
                            <div class="user-info-name">{{name}}</div>
                            <br/>
                            <div>用户秘钥：{{userKey}}</div>
                            <br/>
                            <div>用户邮箱：{{email}}</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover" style="height:252px;">
                    <div slot="header" class="clearfix">
                        <span>现有环境</span>
                    </div>
                    <el-table
                            :data="this.env.envList"
                            :show-header="false"
                            class="table"
                            header-cell-class-name="table-header"
                            :default-sort="{prop: 'createTime', order: 'descending'}"
                    >
                        <el-table-column
                                width="%10"
                                prop="index"
                                type="index"
                                align="center">
                        </el-table-column>
                        <el-table-column
                                prop="envName"
                                width="%20">
                        </el-table-column>
                        <el-table-column
                                prop="createTime"
                                label="创建日期"
                                sortable
                                width="%20">
                        </el-table-column>
                    </el-table>
                    <div class="pagination">
                        <el-pagination
                                background
                                layout="total"
                                :total="query.totalCount"
                        ></el-pagination>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover" style="height:252px;">
                    <div slot="header" class="clearfix">
                        <span>gpu</span>
                    </div>
                    <el-table :show-header="false" :data="gpu.gpuList" style="width:100%;">
                        <el-table-column
                                prop="number"
                                label="编号"
                                sortable
                        >
                        </el-table-column>
                        <el-table-column
                                prop="name"
                                label="名称"
                                sortable
                        >
                        </el-table-column>
                        <el-table-column
                                prop="status"
                                label="状态"
                        >
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>
        <el-row :gutter="20">
            <el-col :span="12">
                <el-card shadow="hover" style="height:400px;">
                    <div slot="header" class="clearfix">
                        <span>已完成任务</span>
                    </div>
                    <el-table :show-header="false" :data="task.complementTaskList" style="width:100%;">
                        <el-table-column
                                width="%10"
                                prop="index"
                                type="index"
                                align="center">
                        </el-table-column>
                        <el-table-column
                                prop="taskTitle"
                                label="任务名称"
                                sortable
                        >
                        </el-table-column>
                        <el-table-column
                                prop="taskKey"
                                label="任务秘钥"
                                sortable
                        >
                        </el-table-column>
                        <el-table-column
                                prop="createTime"
                                label="创建日期"
                                sortable
                        >
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card shadow="hover" style="height:400px;">
                    <div slot="header" class="clearfix">
                        <span>待完成任务</span>
                    </div>
                    <el-table :show-header="false" :data="task.todoTaskList" style="width:100%;">
                        <el-table-column
                                width="%10"
                                prop="index"
                                type="index"
                                align="center">
                        </el-table-column>
                        <el-table-column
                                prop="taskTitle"
                                label="任务名称"
                                sortable
                        >
                        </el-table-column>
                        <el-table-column
                                prop="taskKey"
                                label="任务秘钥"
                                sortable
                        >
                        </el-table-column>
                        <el-table-column
                                prop="createTime"
                                label="创建日期"
                                sortable
                        >
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>


        <!-- 编辑弹出框 -->
        <!--        <el-dialog title="编辑" :visible.sync="editVisible" width="30%">-->
        <!--            <el-form ref="form" :model="form" label-width="70px">-->
        <!--                <el-form-item label="用户名">-->
        <!--                    <el-input v-model="form.name"></el-input>-->
        <!--                </el-form-item>-->
        <!--                <el-form-item label="地址">-->
        <!--                    <el-input v-model="form.address"></el-input>-->
        <!--                </el-form-item>-->
        <!--            </el-form>-->
        <!--            <span slot="footer" class="dialog-footer">-->
        <!--                <el-button @click="editVisible = false">取 消</el-button>-->
        <!--                <el-button type="primary" @click="saveEdit">确 定</el-button>-->
        <!--            </span>-->
        <!--        </el-dialog>-->
    </div>
</template>

<script>
    import {fetchData} from "../../api";
    import service from "../../utils/request";

    export default {
        name: "userInfo",
        data() {
            return {
                task: {
                    todoTaskList: [],
                    complementTaskList: [],
                    todoCount: 0,
                    complementCount: 0,
                    sumCount: 0
                },
                env: {
                    envList: [],
                    envCount: 0
                },
                gpu: {
                    gpuList: [],
                    gpuCount: 0
                },
                email: localStorage.getItem("ms_email"),
                userKey: localStorage.getItem("ms_userKey"),
                name: localStorage.getItem('ms_nickname'),
                query: {
                    currentPage: 1,
                    pageSize: 4
                },
            };
        },
        created() {
            // this.getData()
            // var count = 0
            this.timer = setInterval(() => {
                //执行内容
                // console.log(count++)
                // service.get("/detectGpu").then(response => {
                // })
                service.get("/getGpuList").then(response => {
                    if (response.code === 207) {
                        this.query.totalCount = response.result.gpuCount;
                        this.gpu.list = response.result.gpuList;
                    }
                })
            }, 1000 * 6 * 5);
        },
        mounted() {
            this.getData()

        },
        beforeDestroy() {
            clearInterval(this.timer);
        },
        methods: {
            getData() {
                console.log(this)
                service.get("/getEnvListByUserKey?userKey=" + this.userKey + "&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize).then(response => {
                    if (response.code === 207) {
                        this.env.envList = response.result.envList
                        this.env.envCount = response.result.totalCount
                    } else {
                        this.$message.error("环境数据更新失败")
                    }
                });
                service.get("/getGpuList").then(response => {
                    if (response.code === 207) {
                        this.gpu.gpuCount = response.result.gpuCount;
                        this.gpu.gpuList = response.result.gpuList;
                    } else {
                        this.$message.error("gpu数据初始化失败")
                    }
                });
                service.get("/getTaskListByUserKey?userKey=" + this.userKey + "&status=1&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize).then(response => {
                    if (response.code === 207) {
                        this.task.todoTaskList = response.result.taskList;
                        this.task.todoCount = response.result.totalCount;
                    }
                    // else {
                    //     this.$message.error("待完成任务数据初始化失败")
                    // }
                });
                service.get("/getTaskListByUserKey?userKey=" + this.userKey + "&status=-1&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize).then(response => {
                    if (response.code === 207) {
                        this.task.complementTaskList = response.result.taskList;
                        this.task.complementCount = response.result.totalCount;
                    }
                    // else {
                    //     this.$message.error("已完成任务数据初始化失败")
                    // }
                })
            },
        }
    }
</script>

<style scoped>
    .table {
        /*width: 100%;*/
        font-size: 14px;
    }


    .red {
        color: #ff0000;
    }

    .mr10 {
        margin-right: 10px;
    }

    .user-info {
        display: flex;
        align-items: center;
        padding-bottom: 20px;
        border-bottom: 2px solid #ccc;
        margin-bottom: 20px;
    }

    .user-avator {
        width: 120px;
        height: 120px;
        border-radius: 50%;
    }

    .user-info-cont {
        padding-left: 50px;
        flex: 1;
        font-size: 14px;
        color: #999;
    }

    .user-info-cont div:first-child {
        font-size: 30px;
        color: #222;
    }

    .user-info-list span {
        margin-left: 70px;
    }

    .mgb20 {
        margin-bottom: 20px;
    }

    .table-td-thumb {
        display: block;
        margin: auto;
        width: 40px;
        height: 40px;
    }
</style>
