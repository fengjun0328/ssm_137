package cn.smbms.service.user;

import java.util.List;

import cn.smbms.dao.user.UserDao;
import cn.smbms.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource(name = "userDao")
    private UserDao userDao;

    @Override
    public boolean add(User user) {
        boolean flag = false;
        try {
            int updateRows = userDao.add(user);
            if (updateRows > 0) {
                flag = true;
            } else {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public User login(String userCode, String userPassword) {
        User user = null;
        try {
            user = userDao.getLoginUser(userCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //匹配密码
        if (null != user) {
            if (!user.getUserPassword().equals(userPassword))
                user = null;
        }
        return user;
    }

    @Override
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        List<User> userList = null;
        try {
            userList = userDao.getUserList(queryUserName, queryUserRole, (currentPageNo-1)*pageSize, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public User selectUserCodeExist(String userCode) {
        User user = null;
        try {
            user = userDao.getLoginUser( userCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean deleteUserById(Integer delId) {
        boolean flag = false;
        try {
            if (userDao.deleteUserById(delId) > 0)
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public User getUserById(String id) {
        User user = null;
        try {
            user = userDao.getUserById( id);
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }
        return user;
    }

    @Override
    public boolean modify(User user) {
        boolean flag = false;
        try {
            if (userDao.modify(user) > 0)
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean updatePwd(int id, String pwd) {
        boolean flag = false;
        try {
            if (userDao.updatePwd(id, pwd) > 0)
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public int getUserCount(String queryUserName, Integer queryUserRole) {
        int count = 0;
        try {
            count = userDao.getUserCount( queryUserName, queryUserRole);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

}
