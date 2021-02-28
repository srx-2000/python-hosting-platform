import axios from 'axios';
import {showFullScreenLoading, tryHideFullScreenLoading} from "./serverHelper";

const service = axios.create({
    // process.env.NODE_ENV === 'development' 来判断是否开发环境
    // easy-mock服务挂了，暂时不使用了
    baseURL: 'http://localhost:8888',
    timeout: 1000 * 300
});

service.interceptors.request.use(
    config => {
        console.log("我是请求拦截器，我拦截到了一个请求")
        if (config.showLoading) {
            showFullScreenLoading()
        }
        // showFullScreenLoading();
        return config;
    },
    error => {
        console.log(error);
        return Promise.reject(error);
    }
);

service.interceptors.response.use(
    response => {
        // if (response.config.showLoading) {
        // }
        if (response.status === 200) {
            // console.log("ataegfas")
            // console.log(response.data)
            tryHideFullScreenLoading()
            return response.data;
        } else {
            tryHideFullScreenLoading()
            Promise.reject();
        }
    },
    error => {
        console.log(error);
        tryHideFullScreenLoading()
        return Promise.reject();
    }
);

export default service;
