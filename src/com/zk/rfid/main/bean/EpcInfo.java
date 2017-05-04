package com.zk.rfid.main.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: EPC
 * @date:2016-5-14 下午11:18:14
 * @author: ldl
 */
public class EpcInfo implements Serializable
{

	private String materialId;		//物资ID
	private String materialName;//名称
	private String deliveryOrderNumber;//入库单号
	private String materialUnit;//单位
	private String epcCode;//epc码
	private String location;//仓库位置
	private String materialCount;//物资数量
	private String materialStatus;//物资状态
	private String materialType;//物资性质
	private String inDate;//入库时间
	private String isBox;//是否箱子
	private String totalCount;//总数
	private String materialSpecDescribe;//物资规格描述
	private String materialSpec;//规格
	private String isInBox;//是否箱装
	private String positionCode;//位置码,包含架栏层
	private String materialEmergency;//是否应急物资

	private String lendPersonName;//借领人姓名
	private String lendDate;//领出日期
	private String returnPersonName;//归还人
	private String returnDate;//归还日期
	private String handlePersonName;//经办人
	private String arrivedDate;//到货日期
	private String outDate;//出货日期
	private String sendCompany;//送货公司
	private String remarks;//备注

	private String appliedDate;//申请日期
	private String appliedPurpose;//申请用途
	private String appliedJobNumber;//申请人工号
	private String appliedPersonName;//申请人姓名
	private String appliedStatus;//申请状态

	private String approvalOrderNumber;//验收单号
	private String appliedOrderNumber;//申请单号
	private String appliedMaterialNumber;//申请物资的校验
	private String sendOrderRemarks;
	private String appliedOrderRemarks;
	private String deliveryOrderRemarks;

	private String returnCount;
	private String lendCount;
	private String applyCount;
	
	
	private String quality;
	private String plateNumber;
	private String attachment;
	private String sumCount;
	private String materialTotalCount;
	private String lendHandPerson;
	
	private String oldCount;
	private String newEcpCode;

	private int itemColor;//已写标签标记
	
	// 扫描详细
	private String serialNumber;
	// 传递list
	private List<EpcInfo> listEpc;

	public int getItemColor() {
		return itemColor;
	}

	public void setItemColor(int itemColor) {
		this.itemColor = itemColor;
	}

	public String getMaterialId()
	{
		return materialId;
	}

	public void setMaterialId(String materialId)
	{
		this.materialId = materialId;
	}

	public String getMaterialName()
	{
		return materialName;
	}

	public void setMaterialName(String materialName)
	{
		this.materialName = materialName;
	}

	public String getDeliveryOrderNumber()
	{
		return deliveryOrderNumber;
	}

	public void setDeliveryOrderNumber(String deliveryOrderNumber)
	{
		this.deliveryOrderNumber = deliveryOrderNumber;
	}

	public String getMaterialUnit()
	{
		return materialUnit;
	}

	public void setMaterialUnit(String materialUnit)
	{
		this.materialUnit = materialUnit;
	}

	public String getEpcCode()
	{
		return epcCode;
	}

	public void setEpcCode(String epcCode)
	{
		this.epcCode = epcCode;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getMaterialCount()
	{
		return materialCount;
	}

	public void setMaterialCount(String materialCount)
	{
		this.materialCount = materialCount;
	}

	public String getMaterialStatus()
	{
		return materialStatus;
	}

	public void setMaterialStatus(String materialStatus)
	{
		this.materialStatus = materialStatus;
	}

	public String getInDate()
	{
		return inDate;
	}

	public void setInDate(String inDate)
	{
		this.inDate = inDate;
	}

	public String getIsBox()
	{
		return isBox;
	}

	public void setIsBox(String isBox)
	{
		this.isBox = isBox;
	}

	public String getSerialNumber()
	{
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	public List<EpcInfo> getListEpc()
	{
		return listEpc;
	}

	public void setListEpc(List<EpcInfo> listEpc)
	{
		this.listEpc = listEpc;
	}

	public String getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(String totalCount)
	{
		this.totalCount = totalCount;
	}

	public String getLendPersonName()
	{
		return lendPersonName;
	}

	public void setLendPersonName(String lendPersonName)
	{
		this.lendPersonName = lendPersonName;
	}

	public String getLendDate()
	{
		return lendDate;
	}

	public void setLendDate(String lendDate)
	{
		this.lendDate = lendDate;
	}

	public String getReturnPersonName()
	{
		return returnPersonName;
	}

	public void setReturnPersonName(String returnPersonName)
	{
		this.returnPersonName = returnPersonName;
	}

	public String getReturnDate()
	{
		return returnDate;
	}

	public void setReturnDate(String returnDate)
	{
		this.returnDate = returnDate;
	}

	public String getHandlePersonName()
	{
		return handlePersonName;
	}

	public void setHandlePersonName(String handlePersonName)
	{
		this.handlePersonName = handlePersonName;
	}

	public String getArrivedDate()
	{
		return arrivedDate;
	}

	public void setArrivedDate(String arrivedDate)
	{
		this.arrivedDate = arrivedDate;
	}

	public String getOutDate()
	{
		return outDate;
	}

	public void setOutDate(String outDate)
	{
		this.outDate = outDate;
	}

	public String getSendCompany()
	{
		return sendCompany;
	}

	public void setSendCompany(String sendCompany)
	{
		this.sendCompany = sendCompany;
	}

	public String getAppliedDate()
	{
		return appliedDate;
	}

	public void setAppliedDate(String appliedDate)
	{
		this.appliedDate = appliedDate;
	}

	public String getAppliedPurpose()
	{
		return appliedPurpose;
	}

	public void setAppliedPurpose(String appliedPurpose)
	{
		this.appliedPurpose = appliedPurpose;
	}

	public String getAppliedJobNumber()
	{
		return appliedJobNumber;
	}

	public void setAppliedJobNumber(String appliedJobNumber)
	{
		this.appliedJobNumber = appliedJobNumber;
	}

	public String getAppliedPersonName()
	{
		return appliedPersonName;
	}

	public void setAppliedPersonName(String appliedPersonName)
	{
		this.appliedPersonName = appliedPersonName;
	}

	public String getApprovalOrderNumber()
	{
		return approvalOrderNumber;
	}

	public void setApprovalOrderNumber(String approvalOrderNumber)
	{
		this.approvalOrderNumber = approvalOrderNumber;
	}

	public String getAppliedOrderNumber()
	{
		return appliedOrderNumber;
	}

	public void setAppliedOrderNumber(String appliedOrderNumber)
	{
		this.appliedOrderNumber = appliedOrderNumber;
	}

	public String getSendOrderRemarks()
	{
		return sendOrderRemarks;
	}

	public void setSendOrderRemarks(String sendOrderRemarks)
	{
		this.sendOrderRemarks = sendOrderRemarks;
	}

	public String getAppliedOrderRemarks()
	{
		return appliedOrderRemarks;
	}

	public void setAppliedOrderRemarks(String appliedOrderRemarks)
	{
		this.appliedOrderRemarks = appliedOrderRemarks;
	}

	public String getDeliveryOrderRemarks()
	{
		return deliveryOrderRemarks;
	}

	public void setDeliveryOrderRemarks(String deliveryOrderRemarks)
	{
		this.deliveryOrderRemarks = deliveryOrderRemarks;
	}

	public String getMaterialSpecDescribe()
	{
		return materialSpecDescribe;
	}

	public void setMaterialSpecDescribe(String materialSpecDescribe)
	{
		this.materialSpecDescribe = materialSpecDescribe;
	}

	public String getMaterialSpec()
	{
		return materialSpec;
	}

	public void setMaterialSpec(String materialSpec)
	{
		this.materialSpec = materialSpec;
	}

	public String getPositionCode()
	{
		return positionCode;
	}

	public void setPositionCode(String positionCode)
	{
		this.positionCode = positionCode;
	}

	public String getAppliedStatus()
	{
		return appliedStatus;
	}

	public void setAppliedStatus(String appliedStatus)
	{
		this.appliedStatus = appliedStatus;
	}

	public String getReturnCount()
	{
		return returnCount;
	}

	public void setReturnCount(String returnCount)
	{
		this.returnCount = returnCount;
	}

	public String getLendCount()
	{
		return lendCount;
	}

	public void setLendCount(String lendCount)
	{
		this.lendCount = lendCount;
	}

	public String getApplyCount()
	{
		return applyCount;
	}

	public void setApplyCount(String applyCount)
	{
		this.applyCount = applyCount;
	}

	public String getAppliedMaterialNumber()
	{
		return appliedMaterialNumber;
	}

	public void setAppliedMaterialNumber(String appliedMaterialNumber)
	{
		this.appliedMaterialNumber = appliedMaterialNumber;
	}

	public String getIsInBox()
	{
		return isInBox;
	}

	public void setIsInBox(String isInBox)
	{
		this.isInBox = isInBox;
	}

	public String getQuality()
	{
		return quality;
	}

	public void setQuality(String quality)
	{
		this.quality = quality;
	}

	public String getPlateNumber()
	{
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber)
	{
		this.plateNumber = plateNumber;
	}

	public String getAttachment()
	{
		return attachment;
	}

	public void setAttachment(String attachment)
	{
		this.attachment = attachment;
	}

	public String getSumCount()
	{
		return sumCount;
	}

	public void setSumCount(String sumCount)
	{
		this.sumCount = sumCount;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	public String getMaterialTotalCount()
	{
		return materialTotalCount;
	}

	public void setMaterialTotalCount(String materialTotalCount)
	{
		this.materialTotalCount = materialTotalCount;
	}

	public String getLendHandPerson()
	{
		return lendHandPerson;
	}

	public void setLendHandPerson(String lendHandPerson)
	{
		this.lendHandPerson = lendHandPerson;
	}

	public String getOldCount()
	{
		return oldCount;
	}

	public void setOldCount(String oldCount)
	{
		this.oldCount = oldCount;
	}

	public String getNewEcpCode()
	{
		return newEcpCode;
	}

	public void setNewEcpCode(String newEcpCode)
	{
		this.newEcpCode = newEcpCode;
	}

	public String getMaterialType()
	{
		return materialType;
	}

	public void setMaterialType(String materialType)
	{
		this.materialType = materialType;
	}

	public String getMaterialEmergency()
	{
		return materialEmergency;
	}

	public void setMaterialEmergency(String materialEmergency)
	{
		this.materialEmergency = materialEmergency;
	}

}
