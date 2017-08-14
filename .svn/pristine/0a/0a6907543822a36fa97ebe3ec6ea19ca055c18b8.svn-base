/**
 * 
 */
package com.wy.mq.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * TOPIC、TAG、GROUP、VERSION对应Consumer的关系
 * </pre>
 *
 * @author 张克行
 * @since 2016年10月27日
 */
public class MqConfig implements Serializable {

	private static final long	serialVersionUID	= 2014028775509337737L;

	private Long				id;

	private String				clazz;

	private String				application;

	private String				registry;

	private String				registryGroup;

	private String				topic;

	private String				tag;

	private String				group;

	private String				version;

	protected Integer			retries;

	private Date				createTime;

	private Date				updateTime;

	private String				remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getGroup() {
		return group==null ? registryGroup : group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getRetries() {
		return retries;
	}

	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getRegistryGroup() {
		return registryGroup;
	}

	public void setRegistryGroup(String registryGroup) {
		this.registryGroup = registryGroup;
	}

}
