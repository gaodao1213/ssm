package com.soecode.lyf.web;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Heartbeat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.soecode.lyf.dto.AppointExecution;
import com.soecode.lyf.dto.Result;
import com.soecode.lyf.entity.Book;
import com.soecode.lyf.enums.AppointStateEnum;
import com.soecode.lyf.exception.NoNumberException;
import com.soecode.lyf.exception.RepeatAppointException;
import com.soecode.lyf.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/book") // url:/模块/资源/{id}/细分 /seckill/list
public class BookController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BookService bookService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	private String list(Model model) {
		List<Book> list = bookService.getList();
		model.addAttribute("list", list);
		// list.jsp + model = ModelAndView
		return "list";// WEB-INF/jsp/"list".jsp
	}

	@RequestMapping(value = "/{bookId}/detail", method = RequestMethod.GET)
	private String detail(@PathVariable("bookId") Long bookId, Model model) {
		if (bookId == null) {
			return "redirect:/book/list";
		}
		Book book = bookService.getById(bookId);
		if (book == null) {
			return "forward:/book/list";
		}
		model.addAttribute("book", book);
		return "detail";
	}

	@ResponseBody
	@RequestMapping(value="/transaction")
	public Map testTranaction(HttpServletRequest request, HttpServletResponse response){
		Map map = new HashMap<>();
		String type = "transactionTest";
		String name = "testname1";
		String testType = request.getParameter("type");
		Transaction t = Cat.newTransaction(type,name);
		try{
			t.addData("transactionDataKey","transactonDataValue");
			if(!StringUtils.isEmpty(testType)){
				int b = 88/0;
			}
			map.put("code",0);
			map.put("dataMap","dmap");
			map.put("msg","success");
			t.setStatus(Message.SUCCESS);
		}catch (Exception e){
			t.setStatus(e);
			Cat.logError(e);
		}finally {
			t.complete();
		}
		return map;
	}

	@ResponseBody
	@RequestMapping(value="/event")
	public Map testEvent(HttpServletRequest request, HttpServletResponse response){
		Map map = new HashMap<>();
		String type = "transactionEvent";
		String name = "transactionEventName";
		String testType = request.getParameter("type");
		Transaction t = Cat.newTransaction(type,name);
		try{
			t.addData("transactionDataKey","transactonDataValue");
			if(!StringUtils.isEmpty(testType)){
				int b = 88/0;
			}
			map.put("code",0);
			map.put("dataMap","dmap");
			map.put("msg","success");
			Cat.logEvent("eventTest","eventTestName");
			t.setStatus(Message.SUCCESS);
		}catch (Exception e){
			t.setStatus(e);
			Cat.logError(e);
		}finally {
			t.complete();
		}
		return map;
	}

	@ResponseBody
	@RequestMapping(value="/metric")
	public Map testMetric(HttpServletRequest request, HttpServletResponse response){
		Map map = new HashMap<>();
		String type = "transactionMetric";
		String name = "transactionMetricName";
		String testType = request.getParameter("type");
		Transaction t = Cat.newTransaction(type,name);
		try{
			t.addData("transactionDataKey","transactonDataValue");
			if(!StringUtils.isEmpty(testType) && "sum".equals(testType)){
				Cat.logMetricForSum("totalCount",3);
			}else if(!StringUtils.isEmpty(testType) && "count".equals(testType)){
				Cat.logMetricForCount("totalCount",2);
			}else{
				Cat.logMetricForCount("totalCount",1);
			}
			map.put("code",0);
			map.put("dataMap","dmap");
			map.put("msg","success");
			Cat.logEvent("eventTest","eventTestName");
			t.setStatus(Message.SUCCESS);
		}catch (Exception e){
			t.setStatus(e);
			Cat.logError(e);
		}finally {
			t.complete();
		}
		return map;
	}
	// ajax json
	@RequestMapping(value = "/{bookId}/appoint", method = RequestMethod.POST, produces = {
			"application/json; charset=utf-8" })
	@ResponseBody
	private Result<AppointExecution> appoint(@PathVariable("bookId") Long bookId, @RequestParam("studentId") Long studentId) {
		if (studentId == null || studentId.equals("")) {
			return new Result<>(false, "学号不能为空");
		}
		AppointExecution execution = null;
		try {
			execution = bookService.appoint(bookId, studentId);
		} catch (NoNumberException e1) {
			execution = new AppointExecution(bookId, AppointStateEnum.NO_NUMBER);
		} catch (RepeatAppointException e2) {
			execution = new AppointExecution(bookId, AppointStateEnum.REPEAT_APPOINT);
		} catch (Exception e) {
			execution = new AppointExecution(bookId, AppointStateEnum.INNER_ERROR);
		}
		return new Result<AppointExecution>(true, execution);
	}

}
