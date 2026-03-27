public <R,ID> R queryWithPassThrough(
        String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit){
    
    String key = keyPrefix + id;
    
    // 【读策略】
    // 1. 先读缓存
    String json = stringRedisTemplate.opsForValue().get(key);
    if (StrUtil.isNotBlank(json)) {
        return JSONUtil.toBean(json, type);  // 命中直接返回
    }
    
    // 2. 缓存不存在，查数据库
    R r = dbFallback.apply(id);  // Lambda 表达式，实际是查询数据库的方法
    
    // 3. 数据库也不存在，写入空值（防止缓存穿透）
    if (r == null) {
        stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
        return null;
    }
    
    // 4. 数据库存在，写入缓存
    this.set(key, r, time, unit);
    return r;
}
