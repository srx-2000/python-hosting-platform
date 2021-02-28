<template>
    <div class="gpu">
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-cpu"></i> gpu信息
                    <el-tooltip class="item" effect="dark" content='系统会每隔5分钟刷新更新一次gpu状态' placement="top-start">
                        <i class="el-icon-question" style="color: rgba(0, 0, 0, 0.5)"></i>
                    </el-tooltip>
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">

            <!--            <el-button type="primary" @click="initGpu" class="handle-box">初始化</el-button>-->
            <el-table
                    :data="gpuList"
                    :default-sort="{prop: 'number', order: 'descending'}"
            >
                <el-table-column
                        prop="number"
                        label="编号"
                        sortable
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="name"
                        label="名称"
                        sortable
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="totalMemory"
                        label="总内存"
                        sortable
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="usedMemory"
                        label="已用内存"
                        sortable
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="usableMemory"
                        label="可用内存"
                        sortable
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="usageRate"
                        label="内存利用率"
                        sortable
                        width="180">
                </el-table-column>
                <el-table-column
                        prop="status"
                        label="状态"
                >
                </el-table-column>
            </el-table>

        </div>
        <div class="container mt10">
            <canvas id="myChart" width="1200" height="472.5"></canvas>
        </div>
    </div>

</template>

<script>
    import service from "../../utils/request";
    import "echarts/theme/macarons";

    export default {
        name: "gpu",
        data() {
            return {
                query: {
                    totalCount: 0
                },
                gpuList: [],
                option: {
                    title: {
                        text: 'gpu使用率',
                        subtext: '12小时内'
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: []
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            mark: {show: true},
                            dataView: {show: true, readOnly: false},
                            magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                            restore: {show: true},
                            saveAsImage: {show: true}
                        }
                    },
                    calculable: true,
                    xAxis: [
                        {
                            type: 'category',
                            boundaryGap: false,
                            data: ["12小时前", "11小时前", "10小时前", "9小时前", "8小时前", "7小时前", "6小时前", "5小时前", "4小时前", "3小时前", "2小时前", "1小时前",]
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value',
                            max:100
                        }
                    ],
                    series: [
                        // {
                        //     name: '成交',
                        //     type: 'line',
                        //     smooth: true,
                        //     itemStyle: {normal: {areaStyle: {type: 'default'}}},
                        //     data: [10, 12, 21, 54, 260, 830, 710]
                        // },
                        // {
                        //     name: '预购',
                        //     type: 'line',
                        //     smooth: true,
                        //     itemStyle: {normal: {areaStyle: {type: 'default'}}},
                        //     data: [30, 182, 434, 791, 390, 30, 10]
                        // },
                        // {
                        //     name:'曹尼玛',
                        //     type:'line',
                        //     smooth:true,
                        //     itemStyle: {normal: {areaStyle: {type: 'default'}}},
                        //     data:[30, 5555, 434, 791, 390, 30, 10]
                        // },
                        // {
                        //     name: '意向',
                        //     type: 'line',
                        //     smooth: true,
                        //     itemStyle: {normal: {areaStyle: {type: 'default'}}},
                        //     data: [1320, 1132, 601, 234, 120, 90, 20]
                        // }
                    ]
                }
            }
        },
        created() {
            // this.timer = setInterval(() => {
            //     this.getData()
            // }, 1000 * 5 * 60);
        },
        mounted() {
            this.getData();
            this.getDraw();
            this.drawLine();
        },
        beforeDestroy() {
            // clearInterval(this.timer)
        },
        methods: {
            getData() {
                service.get("/getGpuList").then(response => {
                    if (response.code === 207) {
                        this.query.totalCount = response.result.gpuCount;
                        this.gpuList = response.result.gpuList;
                        console.log(this.gpuList.length)
                        for (var i = 0; i < this.gpuList.length; i++) {
                            service.get("/getGpuStatusPointList?gpuName=" + this.gpuList[i].name + "&gpuId=" + this.gpuList[i].number).then(response1 => {
                                console.log(response1)
                                if (response1.code === 207) {
                                    this.option.legend.data.push(this.gpuList[i-1].name);
                                    var list = response1.result.map(Number);
                                    var item = {
                                        name: this.gpuList[i-1].name,
                                        type: "line",
                                        smooth: true,
                                        itemStyle: {normal: {areaStyle: {type: 'default'}}},
                                        data: list
                                    };
                                    // this.gpuList.push(item)
                                    console.log()
                                    // console.log("item");
                                    this.option.series.push(item)
                                    let myChart = this.$echarts.init(document.getElementById('myChart'), "macarons");
                                    myChart.setOption(this.option);
                                } else {
                                    this.$message.error("暂无12小时内数据");
                                }

                            })

                        }
                    } else {
                        this.$message.error("gpu数据加载失败")
                    }
                });
            },
            getDraw() {
                console.log("这是进入了")
                console.log(this.gpuList)
                for (var i in this.gpuList) {
                    console.log(i)
                }
                //     } else {
                //         this.$message.error("gpu数据加载失败")
                //     }
                // });

            },
            initGpu() {
                this.axios.post("initGpu").then(response => {
                    if (response.data.code === 433) {
                        this.$message.warning("未检测到新的gpu")
                    } else {
                        this.$message.success("初始化成功")
                    }
                })
            },
            drawLine() {
                // 基于准备好的dom，初始化echarts实例

                // 绘制图表
            }
        }
    }
</script>

<style scoped>
    .handle-box {
        float: right;
        margin-bottom: 20px;
    }

    .mt10 {
        margin-top: 10px;
    }

</style>
