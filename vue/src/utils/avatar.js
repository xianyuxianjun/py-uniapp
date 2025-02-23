/**
 * 头像生成工具类
 */

// 可用的头像服务类型
const AVATAR_STYLES = {
  AVATAAARS: 'avataaars',    // 卡通人物
  BOTTTS: 'bottts',          // 机器人
  PIXEL_ART: 'pixel-art',    // 像素风格
  IDENTICON: 'identicon'     // 几何图案
}

/**
 * 生成随机头像URL
 * @param {string|number} seed - 用于生成唯一头像的种子值
 * @param {string} style - 头像风格,默认为 avataaars
 * @param {Object} options - 其他选项
 * @returns {string} 头像URL
 */
export const generateAvatar = (seed, style = AVATAR_STYLES.AVATAAARS, options = {}) => {
  // 基础URL
  const baseUrl = 'https://api.dicebear.com/7.x'
  
  // 构建参数
  const params = new URLSearchParams({
    seed: seed.toString(),
    ...options
  })

  // 返回完整的头像URL
  return `${baseUrl}/${style}/svg?${params.toString()}`
}

// 导出可用的头像风格
export { AVATAR_STYLES }

/**
 * 生成随机用户头像
 * @param {number} index - 用户索引
 * @returns {string} 头像URL
 */
export const generateUserAvatar = (index) => {
  return generateAvatar(index, AVATAR_STYLES.AVATAAARS, {
    backgroundColor: ['b6e3f4','c0aede','d1d4f9'], // 背景色
    radius: 50, // 圆角
  })
} 