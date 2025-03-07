import axios from 'axios';
import { message } from './message';
import router from '@/router';

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
        console.log('发送请求:', config) // 添加调试日志
        const token = localStorage.getItem('token'); // 从本地存储获取 token
        if (token) {
            config.headers.Authorization = `Bearer ${token}`; // 添加 token 到请求头
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
http.interceptors.response.use(
    (response) => {
        console.log('收到响应:', response) // 添加调试日志
        return response;
    },
    (error) => {
        console.error('请求错误:', error) // 添加错误日志
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    message.error('未授权，请重新登录')
                    router.push('/login')
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
                    message.error(error.response.data?.msg || '请求失败')
            }
        } else if (error.request) {
            // 请求已发出，但没有收到响应
            console.error('无响应:', error.request);
        } else {
            // 其他错误
            message.error('网络错误，请稍后重试')
        }
        return Promise.reject(error);
    }
);

// 导出配置好的 axios 实例
export default http;