package com.zhaoyu.common.starter.redis.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.zhaoyu.common.starter.redis.utils.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

/**
 * @author jiangqiangqiang
 * @description: redis配置类
 * @date 2022/3/8 11:26 AM
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig extends CachingConfigurerSupport {

	/**
	 * redisTemplate配置
	 * <p>
	 * ConditionalOnMissingBean保证只有一个bean
	 * </p>
	 *
	 * @param redisConnectionFactory 工厂
	 * @return /
	 */
	@Bean(name = "redisTemplate")
	@ConditionalOnMissingBean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		// 设置工厂连接
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		// key使用StringRedisSerializer序列化
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = fastJsonRedisSerializer();
		// value使用fastJsonRedisSerializer序列化
		redisTemplate.setValueSerializer(fastJsonRedisSerializer);
		redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
		// 开启事务
		//redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}

	@Bean
	public FastJsonRedisSerializer<Object> fastJsonRedisSerializer() {
		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
		// 开启fastjson autotype功能（不开启，造成EntityWrapper<T>中的T无法正常解析）
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setCharset(StandardCharsets.UTF_8);
		fastJsonConfig.setSerializerFeatures(
				// 输出值为null的字段
				SerializerFeature.WriteMapNullValue,
				// list字段为null，输出[]
				SerializerFeature.WriteNullListAsEmpty,
				// 字符串为null，输出""
				SerializerFeature.WriteNullStringAsEmpty);
		fastJsonRedisSerializer.setFastJsonConfig(fastJsonConfig);
		return fastJsonRedisSerializer;
	}

	/**
	 * redisUtil
	 *
	 * @return /
	 */
	@Bean
	@ConditionalOnBean(name = "redisTemplate")
	public RedisUtil redisUtil() {
		return new RedisUtil();
	}
}
