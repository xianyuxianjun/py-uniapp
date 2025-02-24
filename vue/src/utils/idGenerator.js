/**
 * ID生成器工具类
 */

// 生成指定长度的随机数字字符串
function generateNumericString(length) {
  return Math.random().toString().slice(2, 2 + length);
}

// 生成时间戳部分
function generateTimestamp() {
  return Date.now().toString();
}

/**
 * 生成唯一ID (确保在MySQL INT范围内)
 * @returns {number} 生成的ID
 */
export function generateUserId() {
  const timestamp = generateTimestamp().slice(-6); // 取时间戳后6位
  const random = generateNumericString(3); // 3位随机数
  // MySQL INT UNSIGNED 最大值为 4294967295
  const id = parseInt(timestamp + random);
  return id > 2147483647 ? id % 2147483647 : id; // 确保在INT范围内
}

/**
 * 检查ID是否有效
 * @param {number} id - 要检查的ID
 * @returns {boolean} 是否是有效的ID
 */
export function isValidId(id) {
  return typeof id === 'number' && 
         !isNaN(id) && 
         id > 0 && 
         id <= 2147483647 && // 确保在INT范围内
         Number.isInteger(id);
} 