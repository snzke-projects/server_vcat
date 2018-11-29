package com.vcat.web.kindeditor;

import com.vcat.common.cloud.QCloudClient;
import com.vcat.common.cloud.QCloudUtils;
import com.vcat.common.config.Global;
import com.vcat.module.core.utils.DictUtils;
import com.vcat.module.ec.entity.Customer;
import com.vcat.module.ec.service.CustomerService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("${adminPath}/kindeditor")
public class KindEditorController {
	// 日志输出对象
	private static Logger log = LoggerFactory
			.getLogger(KindEditorController.class);
	// 文件目录名称
	private String fileDir;
	// 文件后缀名称
	private String fileExt;
	// 允许上传文件后缀MAP数组
	private static final HashMap<String, String> extMap = new HashMap<>();
	// 允许上传文件大小MAP数组
	private static final HashMap<String, Long> sizeMap = new HashMap<>();
	// 上传文件存放根目录
	private String filePath = Global.getConfig("temp.file.path");

    // 用于检查用户是否存在
    @Autowired
    private CustomerService customerService;

	static {
		// 初始后缀名称MAP数组
		extMap.put("image", "gif,jpg,jpeg,png,bmp,GIF,JPG,JPEG,PNG,BMP");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "doc,docx,xls,xlsx,ppt,txt,zip,rar");
		// 初始文件大小MAP数组
		sizeMap.put("flash", 30 * 1024 * 1024 * 1024l);
		sizeMap.put("media", 30 * 1024 * 1024 * 1024l);
		sizeMap.put("file", 10 * 1024 * 1024 * 1024l);
	}

	/**
	 * @category 文件、图片上传
	 * @param imgFile
	 * @param widthScale 宽度比例
	 * @param heightScale 高度比例 例：widthScale = 16, heightScale = 9 那么限定图片比例为16: 9
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.ALL_VALUE)
	@ResponseBody
	public Map<String, Object> upload(MultipartFile imgFile,Float widthScale,Float heightScale,
			Boolean checkShop, String dir) throws IOException {
		// 初始相关变量
		Map<String, Object> execute = new HashMap<>();
		if (null == dir || dir.isEmpty()) {
			fileDir = "file";
		}else{
            fileDir = dir;
        }
		// 检查是否有上传文件
		if (null == imgFile) {
			execute.put("error", 1);
			execute.put("message", "请选择上传文件.");
			return execute;
		}
		// 检查上传文件保存目录是否存在或可写
		if (!isExistOrRwFolder()) {
			execute.put("error", 1);
			execute.put("message", "上传文件保存目录不存在或\n是没有写入权限,请检查.");
			return execute;
		}
		// 检查目录名称是否正确
		if (!extMap.containsKey(fileDir)) {
			execute.put("error", 1);
			execute.put("message", "目录名不正确,请检查.");
			return execute;
		}
		// 计算出文件后缀名
        String fileName = imgFile.getOriginalFilename();

		fileExt = FilenameUtils.getExtension(fileName);
		// 检查上传文件类型
        if (checkFileType(execute)) return execute;

        // 检查上传文件的高宽比例
		BufferedImage image = ImageIO.read(imgFile.getInputStream());
		int width = image.getWidth();
		int height = image.getHeight();
        if (checkFileWidth(widthScale, heightScale, execute, width, height)) return execute;

        // 检查上传文件的大小
        if (checkFileSize(imgFile, execute)) return execute;

        Customer customer = null;
        if (null != checkShop && checkShop){
            String phone = FilenameUtils.getBaseName(fileName);
            if(phone.indexOf("_") > 0){
                phone = phone.split("_")[0];
            }
            customer = customerService.getByPhone(phone);
            if(null == customer){
                execute.put("error", 1);
                execute.put("message", "该手机号未注册！");
                return execute;
            }
        }

		File tmpFile = new File(newSavePath());
		imgFile.transferTo(tmpFile);
		// 拷贝上传文件至指定存放目录
		String id = QCloudClient.uploadImage(tmpFile);
		if (id == null) {
			execute.put("error", 1);
			execute.put("message", "上传文件失败！");
			return execute;
		}
        log.info("图片上传完成[" + id + "]");

        String url = QCloudUtils.createOriginalDownloadUrl(id);
        if(null != customer){
            url += "?shopId=" + customer.getId() + "&shopName=" + customer.getUserName() + "&phoneNumber=" + customer.getPhoneNumber();
        }
        tmpFile.delete();

		// 返回上传文件的输出路径至前端
		execute.put("error", 0);
		execute.put("url", url);
		execute.put("width",width + "");
		execute.put("height",height + "");
		return execute;
	}

    /**
     * 检查文件格式
     * @param execute
     * @return
     */
    private boolean checkFileType(Map<String, Object> execute) {
        if (!Arrays.asList(extMap.get(fileDir).split(",")).contains(
                fileExt)) {
            execute.put("error", 1);
            execute.put("message", "上传文件的格式被拒绝,\n只允许" + extMap.get(fileDir)
                    + "格式的文件.");
            return true;
        }
        return false;
    }

    /**
     * 检查图片文件高宽比
     * @param widthScale
     * @param heightScale
     * @param execute
     * @param width
     * @param height
     * @return
     */
    private boolean checkFileWidth(Float widthScale, Float heightScale, Map<String, Object> execute, int width, int height) {
        if(null != widthScale && widthScale > 0 && null != heightScale && heightScale > 0){
            if((widthScale/heightScale) != (new Float(width)/new Float(height))){
                execute.put("error", 1);
                execute.put("width",width);
                execute.put("height",height);
                execute.put("message", "上传的图片宽高比例必须为"+widthScale.intValue()+":"+heightScale.intValue()+"！");
                return true;
            }
        }
        return false;
    }

    /**
     * 检查上传文件大小
     * @param imgFile
     * @param execute
     * @return
     */
    private boolean checkFileSize(MultipartFile imgFile, Map<String, Object> execute) {
        setImageSizeLimit();
        long maxSize = sizeMap.get(fileDir);
        if (imgFile.getSize() > maxSize) {
            execute.put("error", 1);
            String size = null;
            if (maxSize < 1024) {
                size = maxSize + "B";
            }
            if (maxSize >= 1024 && maxSize < 1024 * 1024) {
                size = maxSize / 1024 + "KB";
            }
            if (maxSize > 1024 * 1024) {
                size = maxSize / (1024 * 1024) + "MB";
            }
            execute.put("message", "上传文件大小超过限制,只允\n许上传小于 " + size + " 的文件.");
            return true;
        }
        return false;
    }

    private static void setImageSizeLimit(){
        String imageSizeLimit = DictUtils.getDictValue("ec_image_size_limit","1024");
        Long imageSize;
        try {
            imageSize = Long.parseLong(imageSizeLimit);
        } catch (NumberFormatException e) {
            imageSize = 1024l;
        }
        sizeMap.put("image", imageSize * 1024l);
    }

	/**
	 * 判断文件上传保存的文件夹是否存在或可写
	 * 
	 * @return 如果存在且可写返回"true",否则返回"false"
	 */
	public boolean isExistOrRwFolder() {
		File folder = new File(filePath);
        log.debug("上传图片零时文件夹：" + folder.getAbsolutePath());
		// 文件路径不存在则创建目录
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (!folder.isDirectory())
			return false;
		if (!folder.canWrite())
			return false;
		return true;
	}

	/**
	 * @category 生成新的文件名,且按日期分类管理
	 */
	public String newSavePath() {
		StringBuilder tempPath = new StringBuilder(filePath);
		tempPath.append("/");
		File temp = new File(tempPath.toString());
		if (!temp.exists())
			temp.mkdirs();
		SimpleDateFormat fileNameFormat = new SimpleDateFormat(
				"yyyyMMddkkmmss_S");
		tempPath.append("/").append(fileNameFormat.format(new Date()));
		tempPath.append(".").append(fileExt);
		return tempPath.toString().replaceAll("\\\\", "/");
	}

	/**
	 * @category 拷贝文件
	 * @param src
	 *            源文件
	 * @param tar
	 *            目标路径
	 */
	public void copy(InputStream src, String tar) {
		// 判断源文件或目标路径是否为空
		if (null == src || null == tar || tar.isEmpty()) {
			return;
		}
		// InputStream srcIs = null;
		OutputStream tarOs = null;
		try {
			// srcIs = new FileInputStream(src);
			File tarFile = new File(tar);
			tarOs = new FileOutputStream(tarFile);
			byte[] buffer = new byte[4096];
			int n = 0;
			while (-1 != (n = src.read(buffer))) {
				tarOs.write(buffer, 0, n);
			}
		} catch (IOException e) {
			log.error("Copy File is Fali, Because " + e);
		} finally {
			try {
				if (null != src) {
					src.close();
				}
				if (null != tarOs) {
					tarOs.close();
				}
			} catch (IOException e) {
				log.error("Close Stream is Fail, Because " + e);
			}
		}
	}
}
