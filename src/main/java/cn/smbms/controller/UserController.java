package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.role.RoleServiceImpl;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource(name="roleService")
    private RoleService roleService;

    /**
     * 跳转登录页面的方法
     *
     * @return
     */
    @RequestMapping("/login.html")
    public String login() {

        return "login";
    }

    /**
     * 登录方法
     *
     * @param userCode
     * @param userPassword
     * @param session
     * @param request
     * @return
     */
    @RequestMapping(value = "/dologin.html", method = RequestMethod.POST)
    public String doLogin(String userCode, String userPassword,
                          HttpSession session, HttpServletRequest request) {
        User user = userService.login(userCode, userPassword);
        if (user == null) {
//            request.setAttribute("error", "用户名和密码不符");
            throw new RuntimeException("用户名和密码不符");
//            return "login";
        } else {
            session.setAttribute(Constants.USER_SESSION, user);
            return "redirect:/user/frame.html";
        }
    }

  /*  @ExceptionHandler(value = {RuntimeException.class})
    public String error(RuntimeException e,HttpSession session){
        session.setAttribute("e",e);
        return "error";
    }*/


    /**
     * 权限的方法
     *
     * @param session
     * @return
     */
    @RequestMapping("/frame.html")
    public String main(HttpSession session) {
        if (session.getAttribute(Constants.USER_SESSION) != null) {
            return "frame";
        }
        return "login";
    }

    /**
     * 登出的方法
     *
     * @param session
     * @return
     */
    @RequestMapping("/loginOut.html")
    public String loginOut(HttpSession session) {
        session.removeAttribute(Constants.USER_SESSION);
        return "login";
    }

    /**
     * 获取用户的列表，
     *
     * @return
     */
    @RequestMapping("/userlist.html")
    public String getUsers(@RequestParam(value = "queryname" ,required = false) String queryname,
                           @RequestParam(value = "queryUserRole" ,required = false) String queryUserRole,
                           @RequestParam(value = "pageIndex" ,required = false) String pageIndex,
                           HttpServletRequest request) {
        //查询用户列表

        int userRole = 0;
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        if (queryname == null) {
            queryname = "";
        }
        if (queryUserRole != null && !queryUserRole.equals("")) {
            userRole = Integer.parseInt(queryUserRole);
        }

        if (pageIndex != null) {
            try {
                currentPageNo = Integer.valueOf(pageIndex);
            } catch (NumberFormatException e) {
                return  "error.jsp";
            }
        }
        //总数量（表）
        int totalCount = userService.getUserCount(queryname, userRole);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }
        userList = userService.getUserList(queryname, userRole, currentPageNo, pageSize);
        request.setAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        request.setAttribute("roleList", roleList);
        request.setAttribute("queryUserName", queryname);
        request.setAttribute("queryUserRole", queryUserRole);
        request.setAttribute("totalPageCount", totalPageCount);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPageNo", currentPageNo);
        return "userlist";
    }


    /**
     * 跳转添加页面
     * @return
     */
    @RequestMapping("/adduser.html")
    public String addUser(){
        return "useradd";
    }

    /**
     * 注册的方法
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "/save.html" ,method = RequestMethod.POST)
    public String save(User user, HttpSession session,
                       @RequestParam(value = "attr" ,required = false) MultipartFile attr
                        ){
        String idPicPath=upload(session,attr);
        if("useradd".equals(idPicPath)){
            return  "useradd";
        }
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        user.setIdPicPath(idPicPath);
        if(userService.add(user)){
            return "redirect:/user/userlist.html";
        }
        return "useradd";
    }



    /**
     * 查看的方法
     * @param uid
     * @param model
     * @return
     */
  /*  @RequestMapping("/view.html")
    public String getUser(String uid, Model model){
        User user=userService.getUserById(uid);
        model.addAttribute("user",user);
        return "userview";
    }*/

    /**
     * 使用reset风格查看的方法
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/view.html/{id}")
    public String getUser(@PathVariable String id, Model model){
        User user=userService.getUserById(id);
        model.addAttribute("user",user);
        String path=user.getIdPicPath();
        System.out.println("===========>"+path);
        user.setIdPicPath(path.substring(path.lastIndexOf(File.separator)+1));
        System.out.println("============>"+user.getIdPicPath());
        return "userview";
    }

    /**
     * 跳转修改页面的方法
     * @param uid
     * @param model
     * @return
     */
    @RequestMapping("/modify.html")
    public String midify( String uid, Model model){
        User user=userService.getUserById(uid);
        model.addAttribute("user",user);
        System.out.println(user);
        return "usermodify";
    }
    /**
     * 保存修改的方法
     * @param session
     * @return
     */
    @RequestMapping(value = "/domodify.html" ,method = RequestMethod.POST)
    public String doModify(User user,HttpSession session){
        user.setModifyDate(new Date());
        user.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        if(userService.modify(user)){
            return "redirect:/user/userlist.html";
        }
        return "usermodify";
    }


    public String upload(HttpSession session,MultipartFile attr){
        String idPicPath=null;
        if(!attr.isEmpty()){
            //获取文件的路径  File.separator系统的自适应分隔符
            String filePath=session.getServletContext().getRealPath("statics"+ File.separator +"uploadfiles");
            //获取源文件名
            String fileOldName=attr.getOriginalFilename();
            //获取文件的后缀
//            String sufix=fileOldName.substring(fileOldName.lastIndexOf(".")+1,fileOldName.length());
            String sufix= FilenameUtils.getExtension(fileOldName);
            List<String> sufixs= Arrays.asList(new String[]{"jpg","png","jpeg","pneg"});
            if(attr.getSize()>500000){
                session.setAttribute("uploadFileError","文件太大了");
                return "useradd";
            }else if(sufixs.contains(sufix)){
                //重新命名，目的就是解决重名和字符乱码问题
                String fileName=System.currentTimeMillis()+new Random().nextInt(1000000)+"_person."+sufix;
                File file=new File(filePath,fileName);
                if(!file.exists()){
                    file.mkdirs();
                }
                try {
                    //文件上传
                    attr.transferTo(file);
                }catch (Exception e){
                    e.printStackTrace();
                    session.setAttribute("uploadFileError","上传失败");
                    return "useradd";
                }
                idPicPath=filePath+File.separator+fileName;
                System.out.println("=====>"+idPicPath);
            }else{
                session.setAttribute("uploadFileError","文件格式不对");
                return "useradd";
            }
        }
        return  idPicPath;
    }


    @RequestMapping("/useradd_from.html")
    public String useadd(@ModelAttribute("user") User user){
        return  "user/useradd";
    }

    @RequestMapping(value = "/useradd_from.html",method = RequestMethod.POST)
    public String save_user(User user,HttpSession session){
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        if(userService.add(user)){
            return "redirect:/user/userlist.html";
        }
        return  "user/useradd";
    }


    @RequestMapping("/userCodeIsExits.html")
    @ResponseBody
    public  Object  userCodeIsExits(String userCode){
        //判断用户账号是否可用
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            //userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        }else{
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        //把resultMap转为json字符串 输出
        return JSONArray.toJSONString(resultMap);
    }


    @RequestMapping(value = "/user_view")
    @ResponseBody
    public User getUserById(String id){
        try {
            User user=userService.getUserById(id);
            return user;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
