//  常规配置项
axios.defaults.baseURL = 'http://127.0.0.1:8888'; //  请求服务器具体的地址

axios.defaults.withCredentials = true; //  在跨域中允许携带凭证
axios.defaults.header['Content-Type'] = 'application/x-www-form-urlencoded';//  声明传给服务器的数据，通过请求传给服务器的数据application/x-www-form-urlencoded格式
// axios.defaults.headers.common["token"] = token; //  携带token请求头

