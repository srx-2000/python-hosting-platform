## python代码托管平台（java开发）

### 简介

​	本项目开发的最初目的是为了实现一个可以托管python深度学习的平台。使用户可以将深度学习的项目代码放到平台中运行，并获取最终结果。所以，其中也有涉及一部分gpu调度以及gpu监控的模块。现项目仍在开发阶段，不定时更新。

### 功能

​	现阶段主要功能有如下5个模块：

	1. 用户登录、注册
 	2. 用户创建python环境
 	3. 用户创建、编辑、运行任务（python项目）
 	4. gpu状态监督
 	5. 结果获取

### 技术栈

**前端**

 	1. vue
 	2. element-ui
 	3. echarts
 	4. axios

**后端**

	1. springboot
 	2. mybatis
 	3. mysql
 	4. maven

### 项目环境

​	**开发环境**

* Windows 10
* jdk 11
* springboot 2
* mysql-8.0.21

### 项目启动

**后端**

1. 首先需要有springboot2的全套环境：maven、tomcat、jdk、mysql。以及vue相关环境。确保前后端环境没有问题。
2. 之后使用sql目录中的`development.sql`文件在mysql中运行数据库脚本，创建相应的表。
3. 再然后在`resources`目录下找到`config.properties`文件，并将其中的`windows.root.path`、`linux.root.path`设置为你想要存放运行文件的目录。再修改`gpu.statusLine`的数值，确定系统系统判定gpu状态的界限值。
4. 最后只需要愉快的进入`DevelopmentApplication`文件中运行即可启动后端项目。

**前端**

1. 首先进入到项目的util目录下修改`request.js`文件中的`baseURL`，将其改为服务器地址，或本机地址。
2. 然后在项目目录下执行命令`npm run dev`即可运行前端项目。

### 界面展示

**登录界面**

![Image text](https://github.com/srx-2000/python-hosting-platform/tree/master/screenshots/login.png)

**主界面**

![Image text](https://github.com/srx-2000/python-hosting-platform/tree/master/screenshots/main.png)

**任务列表**

![Image text](https://github.com/srx-2000/python-hosting-platform/tree/master/screenshots/task.png)

**gpu列表**

![Image text](https://github.com/srx-2000/python-hosting-platform/tree/master/screenshots/gpu.png)

### 更新日志

* 2021.3.1

  第一次上传，完成了基础功能

### 特别感谢

​	**前端**

​	[前端使用模板github仓库](https://github.com/lin-xin/vue-manage-system)