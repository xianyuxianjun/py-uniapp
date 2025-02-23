import axios from 'axios';

// 创建自定义 axios 实例
const http = axios.create({
    baseURL: 'http://localhost:8080', // 基础 URL
    timeout: 10000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json', // 默认请求头
    },
});

// 请求拦截器
http.interceptors.request.use(
    (config) => {
        // 在发送请求之前做些什么
        const token = localStorage.getItem('token'); // 从本地存储获取 token
        if (token) {
            config.headers.Authorization = `Bearer ${token}`; // 添加 token 到请求头
        }
        return config;
    },
    (error) => {
        // 对请求错误做些什么
        return Promise.reject(error);
    }
);

// 响应拦截器
http.interceptors.response.use(
    (response) => {
        
        return response;
    },
    (error) => {
        // 对响应错误做些什么
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 处理未授权错误
                    console.error('未授权，请重新登录');
                    break;
                case 404:
                    // 处理未找到资源错误
                    console.error('资源未找到');
                    break;
                case 500:
                    // 处理服务器错误
                    console.error('服务器错误');
                    break;
                default:
                    console.error('请求失败:', error.message);
            }
        } else if (error.request) {
            // 请求已发出，但没有收到响应
            console.error('无响应:', error.request);
        } else {
            // 其他错误
            console.error('请求错误:', error.message);
        }
        return Promise.reject(error);
    }
);

// 导出配置好的 axios 实例
export default http;