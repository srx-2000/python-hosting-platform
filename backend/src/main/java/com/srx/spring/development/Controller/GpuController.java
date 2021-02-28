package com.srx.spring.development.Controller;

import com.srx.spring.development.Entities.DTO.ResultMessage;
import com.srx.spring.development.Entities.GPUInfo;
import com.srx.spring.development.Service.GpuService;
import com.srx.spring.development.util.PropertiesLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.srx.spring.development.Enum.ResultCode.*;

@RestController
@Component
public class GpuController {

    @Autowired
    private GpuService gpuService;

    private PropertiesLoader loader = new PropertiesLoader("config.properties");

    private final int interval = 1000 * 60 * 5;
    private final Integer STATUS_LINE = Integer.valueOf(loader.getValue("gpu.statusLine"));

    @PostMapping("/initGpu")
    public ResultMessage initGpu() {
        gpuService.setStatusLine(STATUS_LINE);

        Boolean flag = gpuService.initGpu();
        if (flag) {
            return new ResultMessage(INIT_GPU_SUCCESS, flag);
        } else return new ResultMessage(FAIL_INIT_GPU, flag);

    }

    @GetMapping("/getGpuList")
    public ResultMessage getGpuListFromDatabase() {
        gpuService.setStatusLine(STATUS_LINE);
        List<GPUInfo> gpuListFromDatabase = gpuService.getGpuListFromDatabase();
        Map<String, Object> resultMap = new HashMap<>();
        for (GPUInfo gpuInfo : gpuListFromDatabase) {
            if (gpuInfo.getStatus().equals("0")) {
                gpuInfo.setStatus("空闲中");
            } else if (gpuInfo.getStatus().equals("1")) {
                gpuInfo.setStatus("使用中");
            } else if (gpuInfo.getStatus().equals("-1")) {
                gpuInfo.setStatus("已卸载");
            }
        }
        if (gpuListFromDatabase.size() != 0) {
            resultMap.put("gpuList", gpuListFromDatabase);
            resultMap.put("gpuCount", gpuListFromDatabase.size());
            return new ResultMessage(DATA_RETURN_SUCCESS, resultMap);
        } else {
            resultMap.put("gpuList", null);
            resultMap.put("gpuCount", 0);
            return new ResultMessage(ERROR_NO_DATA, resultMap);
        }
    }

    @GetMapping("/detectGpu")
    @Scheduled(fixedRate = interval)
    public void run() {
        gpuService.setStatusLine(STATUS_LINE);
        gpuService.run();
    }

    @GetMapping("/getGpuStatusPointList")
    public ResultMessage getGpuStatusPointList(@RequestParam String gpuName, @RequestParam String gpuId) {
        List<String> gpuStatusPointList = gpuService.getGpuStatusPointList(gpuName, gpuId);
        if (gpuStatusPointList.size() == 0)
            return new ResultMessage(ERROR_NO_DATA, null);
        else
            return new ResultMessage(DATA_RETURN_SUCCESS, gpuStatusPointList);
    }


}
