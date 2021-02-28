<template>
    <div class="env">
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-cloudy"></i> 创建环境
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <el-form :model="env"
                     :rules="rules"
                     label-width="100px"
                     ref="envForm"
                     :label-position="labelPosition">
                <el-form-item label="环境名称:" class="env-form-content" prop="envName">
                    <el-input v-model="env.envName" placeholder="环境名称" class="env-input mr10"/>
                    <el-tooltip class="item" effect="dark" content='不可以含有/ \\ : * ? < > | " @' placement="top-start">
                        <i class="el-icon-question" style="color: rgba(0, 0, 0, 0.5)"></i>
                    </el-tooltip>
                    <div style="margin: 20px"></div>
                    <el-button size="middle" type="primary" @click="submit('envForm')">创建</el-button>
                </el-form-item>
            </el-form>
            <div class="handle-box">
                <el-button
                        type="primary"
                        icon="el-icon-refresh"
                        class="mr10"
                        @click="refresh">刷新
                </el-button>
                <el-input v-model="query.name" placeholder="环境名" class="handle-input mr10"></el-input>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            </div>
            <div>
                <el-table
                        :data="tableData"
                        class="table"
                        border
                        header-cell-class-name="table-header"
                        :default-sort="{prop: 'createTime', order: 'descending'}"
                        @row-click="getDetails"
                >
                    <el-table-column
                            label="序号"
                            width="%10"
                            prop="index"
                            type="index"
                            :index="get_index"
                            align="center">
                    </el-table-column>
                    <el-table-column
                            prop="envName"
                            label="环境名"
                            sortable
                            width="%20">
                    </el-table-column>
                    <el-table-column
                            prop="createTime"
                            label="创建日期"
                            sortable
                            width="%20">
                    </el-table-column>
                    <el-table-column
                            prop="hasList"
                            label="现有库"
                            width="%50"
                            :show-overflow-tooltip='true'>
                    </el-table-column>
                </el-table>
            </div>
            <div class="pagination">
                <el-pagination
                        background
                        layout="total, prev, pager, next"
                        :current-page="query.currentPage"
                        :page-size="query.pageSize"
                        :total="query.totalCount"
                        @current-change="handlePageChange"
                ></el-pagination>
            </div>
        </div>
        <el-dialog title="现有库" :visible.sync="editVisible" width="40%" :before-close="sure">
            <el-table size="large"
                      :data="this.listData"
                      border
                      header-cell-class-name="table-header">
                <el-table-column
                        label="序号"
                        width="%20"
                        prop="index"
                        type="index"
                        :index="get_index"
                        align="center">
                </el-table-column>
                <el-table-column
                        prop="library"
                        label="名称"
                        sortable
                        width="%40">
                </el-table-column>
                <el-table-column
                        prop="version"
                        label="版本"
                        sortable
                        width="%40">
                </el-table-column>
            </el-table>
            <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="this.sure">确 定</el-button>
                    </span>
        </el-dialog>

    </div>
</template>

<script>
    import service from "../../utils/request";

    export default {
        name: "addTest",
        data() {
            var envNameReg = /[^/\\:*?<>|"@]/;
            var validateEnvName = (rule, value, callback) => {
                if (!envNameReg.test(value)) {
                    callback(new Error("输入的环境名包含非法字符"))
                } else {
                    this.axios.get("/isEnvExist?envName=" + this.env.envName + "&userKey=" + this.env.userKey).then((response => {
                        if (response.data.code === 434) {
                            callback(new Error(response.data.message))
                        } else {
                            callback()
                        }
                    }))
                }
            }
            return {
                editVisible: false,
                tableData: [],
                listData: [],
                labelPosition: 'top',
                query: {
                    address: "",
                    name: "",
                    currentPage: 1,
                    pageSize: 10,
                    totalCount: 0,
                },
                env: {
                    envName: "",
                    userKey: localStorage.getItem("ms_userKey")
                },
                rules: {
                    envName: [
                        {required: true, message: '请输入环境名', trigger: 'blur'},
                        {validator: validateEnvName, trigger: "blur"},
                    ],
                }

            }
        },
        mounted() {
            this.getData()
        },
        methods: {
            sure() {
                this.editVisible = false;
                this.listData = []
            },
            getDetails(row) {
                this.editVisible = true
                // console.log(row.hasList)
                var list = row.hasList.split(" ");

                function nospance(list) {
                    for (var i = 0; i < list.length; i++) {
                        if (list[i] == '' || list[i] == null || typeof (list[i]) == undefined) {
                            list.splice(i, 1);
                            i = i - 1;
                        }
                    }
                    return list
                }

                list = nospance(list)
                var list1 = [];
                for (var i = 0; i < list.length; i++) {
                    if (list[i].includes("\n")) {
                        var strings = list[i].split("\n");
                        for (var j in strings)
                            list1.push(strings[j])
                    } else {
                        list1.push(list[i])
                    }
                }
                list1 = nospance(list1)
                for (var i = 0; i < list1.length / 2; i++) {
                    var item = {
                        library: list1[2 * i],
                        version: list1[(2 * i) + 1]
                    }
                    this.listData.push(item)
                }
            },
            get_index(index) {
                return (this.query.currentPage - 1) * this.query.pageSize + index + 1;
            },
            getData() {
                service.get("/getEnvListByUserKey?userKey=" + this.env.userKey + "&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize).then(response => {
                    if (response.code === 207) {
                        this.tableData = response.result.envList
                        this.query.totalCount = response.result.totalCount
                    } else {
                        this.$message.error("数据更新失败")
                    }
                })
            },
            refresh() {
                this.getData()
            },
            handlePageChange(val) {
                this.$set(this.query, 'currentPage', val);
                this.getData();
            },
            handleSearch() {
                this.query.currentPage = 1;
                if (this.query.name === "" || this.query.name === undefined || this.query.name === null) {
                    this.$message.warning("请输入关键词")
                } else {
                    service.get("/getEnvListByString?userKey=" + this.env.userKey + "&queryString=" + this.query.name + "&currentPage=" + this.query.currentPage + "&pageSize=" + this.query.pageSize)
                        .then(response => {
                            if (response.code === 207) {
                                this.tableData = response.result.envList
                                this.query.totalCount = response.result.totalCount
                            } else {
                                this.tableData = []
                                this.query.totalCount = 0
                                this.$message.warning("没有查询到与之相关的环境")
                            }
                        })
                }
            },
            submit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        service.post('/insertEnv', {
                            envName: this.env.envName,
                            userKey: this.env.userKey
                        }, {showLoading: true}).then((response) => {
                            if (response.code === 210) {
                                this.$message.success(response.message)
                            } else
                                this.$message.error(response.message)
                        }).catch((response) => {
                            console.log(response);
                        })
                    } else {   //校验不通过
                        return false;
                    }
                });

            }

        }
    }
</script>

<style>
    .env-form-content {
        /*margin-left: 50px;*/
    }

    .env-input {
        width: 200px;
    }

    .table {
        /*width: 100%;*/
        font-size: 14px;
    }

    .handle-box {
        margin-bottom: 20px;
        float: right;
    }

    .handle-input {
        width: 300px;
        display: inline-block;
    }

    .mr10 {
        margin-right: 10px;
    }

    .el-tooltip__popper {
        max-width: 20%;
    }

    .el-tooltip__popper, .el-tooltip__popper.is-dark {
        background: #f5f5f5 !important;
        color: #303133 !important;
    }

</style>
