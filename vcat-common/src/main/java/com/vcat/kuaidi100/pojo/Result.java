package com.vcat.kuaidi100.pojo;

import java.util.ArrayList;

import com.vcat.common.mapper.JsonMapper;

public class Result {
	/**
	 * 收货状态
	 */
	public static final String STATE_RECEIVING = "3";
	/**
	 * 通讯状态，可忽略
	 */
	private String status = "0";
	/**
	 * 状态值     名称       含义
	 *   0	     在途	快件处于运输过程中
	 *   1	     揽件	快件已由快递公司揽收
	 *   2	     疑难	快递100无法解析的状态，或者是需要人工介入的状态，比方说收件人电话错误。
	 *   3	     签收	正常签收
	 *   4	     退签	货物退回发货人并签收
	 *   5	     派件	货物正在进行派件
	 *   6	     退回	货物正处于返回发货人的途中
	 *   7	     转投	货物已转交给其他快递公司代为投递。
	 */
	private String state = "0";
	/**
	 * 单号
	 */
	private String nu = "";
	/**
	 * 是否签收标记，明细状态请参考state字段
	 */
	private String ischeck = "0";
	/**
	 * 快递单明细状态标记，暂未实现，可忽略
	 */
	private String condition = "";
	private String message = "";
	private ArrayList<ResultItem> data = new ArrayList<ResultItem>();
	/**
	 * 快递公司编码,一律用小写字母
	 * 快递公司编码				快递公司名称
     * jd                       : 京东
	 * zhongtianwanyun          : 中天万运
	 * shangcheng               : 尚橙
	 * neweggozzo               : 新蛋奥硕
	 * ontrac                   : Ontrac
	 * sevendays                : 七天连锁
	 * mingliangwuliu           : 明亮物流
	 * guotongkuaidi            : 国通快递
	 * auspost                  : 澳大利亚邮政-英文
	 * canpost                  : 加拿大邮政-英文版
	 * canpostfr                : 加拿大邮政-法文版
	 * upsen                    : UPS-en
	 * tnten                    : TNT-en
	 * dhlen                    : DHL-en
	 * shunfengen               : 顺丰-英文版
	 * gongsuda                 : 共速达
	 * kuayue                   : 跨越速递
	 * quanjitong               : 全际通
	 * shengfengwuliu           : 盛丰物流
	 * ups                      : UPS
	 * yibangwuliu              : 一邦速递
	 * youshuwuliu              : 优速物流
	 * yuanweifeng              : 源伟丰
	 * shunfeng                 : 顺丰速运
	 * yuantong                 : 圆通速递
	 * tiantian                 : 天天快递
	 * Zhaijisong               : 宅急送
	 * cces                     : 希伊艾斯
	 * biaojikuaidi             : 彪记快递
	 * xingchengjibian          : 星晨急便
	 * yafengsudi               : 亚风速递
	 * quanritongkuaidi         : 全日通
	 * anxindakuaixi            : 安信达
	 * fenghuangkuaidi          : 凤凰快递
	 * peisihuoyunkuaidi        : 配思货运
	 * ztky                     : 中铁物流
	 * fedex                    : FedEx-国际
	 * aae                      : AAE-中国
	 * datianwuliu              : 大田物流
	 * xinbangwuliu             : 新邦物流
	 * rufengda                 : 如风达
	 * saiaodi                  : 赛澳递
	 * haihongwangsong          : 海红网送
	 * kangliwuliu              : 康力物流
	 * yunda                    : 韵达快运
	 * huitongkuaidi            : 汇通快运
	 * xinhongyukuaidi          : 鑫飞鸿
	 * quanyikuaidi             : 全一快递
	 * minghangkuaidi           : 民航快递
	 * tnt                      : TNT
	 * debangwuliu              : 德邦物流
	 * longbanwuliu             : 龙邦物流
	 * lianhaowuliu             : 联昊通
	 * zhongyouwuliu            : 中邮物流
	 * yuanzhijiecheng          : 元智捷诚
	 * youzhengguoji            : 国际包裹
	 * huaqikuaiyun             : 华企快运
	 * city100                  : 城市100
	 * yuanchengwuliu           : 远成物流
	 * anjiekuaidi              : 安捷快递
	 * dpex                     : DPEX
	 * zhimakaimen              : 芝麻开门
	 * disifang                 : 递四方
	 * emsguoji                 : EMS-国际
	 * fedexus                  : FedEx-美国
	 * santaisudi               : 三态速递
	 * jinyuekuaidi             : 晋越快递
	 * lejiedi                  : 乐捷递
	 * zhongxinda               : 忠信达
	 * jialidatong              : 嘉里大通
	 * ocs                      : OCS
	 * usps                     : USPS
	 * meiguokuaidi             : 美国快递
	 * lijisong                 : 立即送
	 * yinjiesudi               : 银捷速递
	 * menduimen                : 门对门
	 * hebeijianhua             : 河北建华
	 * weitepai                 : 微特派
	 * fengxingtianxia          : 风行天下
	 * Suer                     : 速尔快递
	 * guangdongyouzhengwuliu   : 广东邮政
	 * Shenghuiwuliu            : 盛辉物流
	 * Changyuwuliu             : 长宇物流
	 * Feikangda                : 飞康达
	 * Youzhengguonei           : 包裹/平邮
	 * wanjiawuliu              : 万家物流
	 * wenjiesudi               : 文捷航空
	 * quanchenkuaidi           : 全晨快递
	 * jiayiwuliu               : 佳怡物流
	 * kuaijiesudi              : 快捷速递
	 * dsukuaidi                : D速快递
	 * ganzhongnengda           : 港中能达
	 * yuefengwuliu             : 越丰物流
	 * jixianda                 : 急先达
	 * baifudongfang            : 百福东方
	 * bht                      : BHT
	 * vancl                    : 凡客
	 * huiqiangkuaidi           : 汇强快递
	 * xiyoutekuaidi            : 希优特
	 * haoshengwuliu            : 昊盛物流
	 * wuyuansudi               : 伍圆速递
	 * lanbiaokuaidi            : 蓝镖快递
	 * coe                      : COE
	 * nanjing                  : 南京100
	 * hengluwuliu              : 恒路物流
	 * jindawuliu               : 金大物流
	 * huaxialongwuliu          : 华夏龙
	 * yuntongkuaidi            : 运通中港
	 * jiajiwuliu               : 佳吉快运
	 * yuananda                 : 源安达
	 * jiayunmeiwuliu           : 加运美
	 * wanxiangwuliu            : 万象物流
	 * hongpinwuliu             : 宏品物流
	 * gls                      : GLS
	 * shangda                  : 上大物流
	 * zhongtiewuliu            : 中铁快运
	 * yuanfeihangwuliu         : 原飞航
	 * shentong                 : 申通快递
	 * haimengsudi              : 海盟速递
	 * shenganwuliu             : 圣安物流
	 * yitongfeihong            : 一统飞鸿
	 * haiwaihuanqiu            : 海外环球
	 * lianbangkuaidi           : 联邦快递
	 * feikuaida                : 飞快达
	 * zhongtong                : 中通速递
	 * ems                      : EMS
	 * jinguangsudikuaijian     : 京广速递
	 * dhl                      : DHL
	 * tiandihuayu              : 天地华宇
	 * xinfengwuliu             : 信丰物流
	 * quanfengkuaidi           : 全峰快递
	 * dhlde                    : DHL-德国
	 * tonghetianxia            : 通和天下
	 * zhengzhoujianhua         : 郑州建华
	 * emsen                    : EMS-英文
	 * hkpost                   : 香港邮政
	 * bangsongwuliu            : 邦送物流
	 * sxhongmajia              : 山西红马甲
	 * suijiawuliu              : 穗佳物流
	 * feibaokuaidi             : 飞豹快递
	 * chuanxiwuliu             : 传喜物流
	 * jietekuaidi              : 捷特快递
	 * longlangkuaidi           : 隆浪快递
	 * zhongsukuaidi            : 中速快递
	 */
	private String com = "";

	@SuppressWarnings("unchecked")
	public Result clone() {
		Result r = new Result();
		r.setCom(this.getCom());
		r.setIscheck(this.getIscheck());
		r.setMessage(this.getMessage());
		r.setNu(this.getNu());
		r.setState(this.getState());
		r.setStatus(this.getStatus());
		r.setCondition(this.getCondition());
		r.setData((ArrayList<ResultItem>) this.getData().clone());

		return r;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNu() {
		return nu;
	}

	public void setNu(String nu) {
		this.nu = nu;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public ArrayList<ResultItem> getData() {
		return data;
	}

	public void setData(ArrayList<ResultItem> data) {
		this.data = data;
	}

	public String getIscheck() {
		return ischeck;
	}

	public void setIscheck(String ischeck) {
		this.ischeck = ischeck;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		return JsonMapper.toJsonString(this);
	}
}
