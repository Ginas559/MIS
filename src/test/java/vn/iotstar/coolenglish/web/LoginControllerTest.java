package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

class LoginControllerTest {

    @Test
    void shouldResolveStudentCourseListAfterLogin() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        LoginController controller = new LoginController();
        Method resolveTargetPath = LoginController.class.getDeclaredMethod("resolveTargetPath", UserAccount.class);
        resolveTargetPath.setAccessible(true);

        String path = (String) resolveTargetPath.invoke(controller,
                new UserAccount("student@coolenglish.vn", "student", "secret", UserRole.STUDENT));

        assertEquals("/course?msg=login_success", path);
    }
}
