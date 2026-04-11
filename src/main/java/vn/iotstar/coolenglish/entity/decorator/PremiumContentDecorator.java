package vn.iotstar.coolenglish.entity.decorator;

import vn.iotstar.coolenglish.entity.AcademicContent;
import vn.iotstar.coolenglish.enums.EnrollmentStatus;

public class PremiumContentDecorator extends BaseContentDecorator {

    private final boolean tuitionPaid;

    public PremiumContentDecorator(AcademicContent wrappedContent, EnrollmentStatus enrollmentStatus) {
        this(wrappedContent, isEligible(enrollmentStatus));
    }

    public PremiumContentDecorator(AcademicContent wrappedContent, boolean tuitionPaid) {
        super(wrappedContent);
        this.tuitionPaid = tuitionPaid;
    }

    public boolean isTuitionPaid() {
        return tuitionPaid;
    }

    @Override
    public String displayContent() {
        if (!tuitionPaid) {
            return "Noi dung Premium. Vui long hoan tat hoc phi de tiep tuc hoc bai nay.";
        }
        return wrappedContent.displayContent();
    }

    private static boolean isEligible(EnrollmentStatus enrollmentStatus) {
        return enrollmentStatus == EnrollmentStatus.STUDYING || enrollmentStatus == EnrollmentStatus.COMPLETED;
    }
}

