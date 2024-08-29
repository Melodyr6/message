package com.aaa.controller;

import com.aaa.entity.CWarehuose;
import com.aaa.entity.Message;
import com.aaa.entity.MessageVo;
import com.aaa.entity.User;
import com.aaa.enumpakage.MessageEnum;
import com.aaa.mapper.MessageMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("history")
public class MessageController {
    @Autowired
    private MessageMapper messageMapper;
    private RestTemplate restTemplate = new RestTemplate();


    @GetMapping("listMessagePage")
    @ResponseBody
    public Object listMessagePage(HttpServletRequest request, MessageVo messageVo, Integer page, Integer limit) {
        User user = (User) request.getSession().getAttribute("user");
        PageHelper.startPage(page, limit);
        String url = "http://localhost:8082/history/getUserid";
        // 创建 HTTP 实体
        HttpEntity<?> requestEntity = new HttpEntity<>(user);

        // 使用 RestTemplate 的 exchange 方法处理泛型
        ResponseEntity<Integer> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Integer>() {}
        );

        // 提取响应体
        Integer id = responseEntity.getBody();

        System.out.println(id);
        messageVo.setReceiverId(id);
        List<Message> messageList = messageMapper.listMessage(messageVo);
        messageList.forEach(message -> {
            message.setMessageTypeName(MessageEnum.getMessageTypeName(message.getMessageType()));
        });
        PageInfo pageInfo = new PageInfo(messageList);
        Map<String, Object> tableData = new HashMap<String, Object>();
        //这是layui要求返回的json数据格式，如果后台没有加上这句话的话需要在前台页面手动设置
        tableData.put("code", 0);
        tableData.put("msg", "");
        //将全部数据的条数作为count传给前台（一共多少条）
        tableData.put("count", pageInfo.getTotal());
        //将分页后的数据返回（每页要显示的数据）
        tableData.put("data", pageInfo.getList());
        return tableData;
    }

    @GetMapping("updateIsRead")
    @ResponseBody
    public Object updateIsRead(MessageVo messageVo) {
        int resultStatus = messageMapper.updateIsRead(messageVo.getMessageId());
        return resultStatus;
    }
}
