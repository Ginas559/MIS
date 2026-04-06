package vn.iotstar.coolenglish.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.ICourseService;
import vn.iotstar.coolenglish.service.impl.CourseServiceImpl;
import vn.iotstar.coolenglish.service.proxy.CourseServiceProxy;

class SecurityAccessFactoryTest {

    @Test
    void factoryShouldReturnRealServiceForAdmin() {
        ICourseService service = SecurityAccessFactory.getInstance()
                .getCourseService(new UserAccount("admin@test.com", "admin", "admin123", UserRole.ADMIN));

        assertInstanceOf(CourseServiceImpl.class, service);
    }

    @Test
    void factoryShouldReturnProxyForNonAdmin() {
        ICourseService service = SecurityAccessFactory.getInstance()
                .getCourseService(new UserAccount("teacher@test.com", "teacher", "teacher123", UserRole.TEACHER));

        assertInstanceOf(CourseServiceProxy.class, service);
    }

    @Test
    void factoryShouldBeSingleton() {
        assertSame(SecurityAccessFactory.getInstance(), SecurityAccessFactory.getInstance());
    }

    @Test
    void proxyShouldBlockFeeUpdateForNonAdmin() {
        StubCourseService stub = new StubCourseService();
        CourseServiceProxy proxy = new CourseServiceProxy(stub, new UserAccount("student@test.com", "student", "student123", UserRole.STUDENT));

        assertThrows(SecurityException.class, () -> proxy.updateCourseFee("C01", 199.0));
        assertFalse(stub.updated);
    }

    @Test
    void proxyShouldAllowListingCourses() {
        StubCourseService stub = new StubCourseService();
        CourseServiceProxy proxy = new CourseServiceProxy(stub, new UserAccount("staff@test.com", "staff", "staff123", UserRole.STAFF));

        assertEquals(stub.findAll(), proxy.findAll());
    }

    private static final class StubCourseService implements ICourseService {

        private boolean updated;
        private final List<Course> courses = List.of(new Course("C01", "English Basics", null, null, null, 150.0,
                "ACTIVE"));

        @Override
        public void updateCourseFee(String courseID, Double newFee) {
            updated = true;
        }

        @Override
        public List<Course> findAll() {
            return courses;
        }
    }
}


