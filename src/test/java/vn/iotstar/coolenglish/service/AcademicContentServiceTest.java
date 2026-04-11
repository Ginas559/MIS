package vn.iotstar.coolenglish.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.dao.impl.ModuleDAO;
import vn.iotstar.coolenglish.dao.impl.RoadmapAccessGrantDAO;
import vn.iotstar.coolenglish.dao.impl.RoadmapDAO;
import vn.iotstar.coolenglish.entity.Lesson;
import vn.iotstar.coolenglish.entity.Module;
import vn.iotstar.coolenglish.entity.Roadmap;
import vn.iotstar.coolenglish.enums.UserRole;

class AcademicContentServiceTest {

    @Test
    void shouldApplyPremiumAndWatermarkDecorators() {
        Module root = new Module("Root", "Demo root");
        root.add(new Lesson("Intro", "Noi dung intro", "READING", "https://learning.local/intro"));

        ModuleDAO moduleDAO = new ModuleDAO() {
            @Override
            public Module findByIdWithChildren(Long moduleId) {
                return root;
            }
        };

        RoadmapDAO roadmapDAO = new RoadmapDAO() {
            @Override
            public Roadmap findByCode(String roadmapCode) {
                Roadmap roadmap = new Roadmap();
                roadmap.setRoadmapCode("TOEIC_RL");
                roadmap.setRootModule(root);
                return roadmap;
            }
        };

        RoadmapAccessGrantDAO grantDAO = new RoadmapAccessGrantDAO() {
            @Override
            public boolean hasActiveGrant(String roadmapCode, Long personId) {
                return true;
            }
        };

        AcademicContentService service = new AcademicContentService(moduleDAO, roadmapDAO, grantDAO);
        AcademicContentService.LearningViewModel vm = service.buildLearningView("TOEIC_RL", 100L,
                "student@coolenglish.vn", null, UserRole.STUDENT);

        assertNotNull(vm.getCurrentContent());
        assertTrue(vm.getCurrentContent().displayContent().contains("student@coolenglish.vn"));
        assertTrue(vm.getCurrentContent().displayContent().contains("https://learning.local/intro"));
    }

    @Test
    void shouldAllowAdminWithoutManualGrant() {
        Module root = new Module("Root", "Demo root");
        root.add(new Lesson("Intro", "Noi dung intro", "READING", "https://learning.local/intro"));

        ModuleDAO moduleDAO = new ModuleDAO() {
            @Override
            public Module findByIdWithChildren(Long moduleId) {
                return root;
            }
        };

        RoadmapDAO roadmapDAO = new RoadmapDAO() {
            @Override
            public Roadmap findByCode(String roadmapCode) {
                Roadmap roadmap = new Roadmap();
                roadmap.setRoadmapCode("IELTS");
                roadmap.setRootModule(root);
                return roadmap;
            }
        };

        RoadmapAccessGrantDAO grantDAO = new RoadmapAccessGrantDAO() {
            @Override
            public boolean hasActiveGrant(String roadmapCode, Long personId) {
                return false;
            }
        };

        AcademicContentService service = new AcademicContentService(moduleDAO, roadmapDAO, grantDAO);
        AcademicContentService.LearningViewModel vm = service.buildLearningView("IELTS", 100L,
                "admin@coolenglish.vn", null, UserRole.ADMIN);

        assertNotNull(vm.getCurrentContent());
        assertTrue(vm.getCurrentContent().displayContent().contains("https://learning.local/intro"));
        assertTrue(vm.getCurrentContent().displayContent().contains("admin@coolenglish.vn"));
    }
}

