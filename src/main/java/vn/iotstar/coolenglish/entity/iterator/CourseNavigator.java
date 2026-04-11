package vn.iotstar.coolenglish.entity.iterator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import vn.iotstar.coolenglish.entity.AcademicContent;

public class CourseNavigator implements Iterator<AcademicContent> {

    private final List<AcademicContent> orderedLeaves;
    private int cursor;

    public CourseNavigator(AcademicContent root) {
        this.orderedLeaves = flattenLeaves(root);
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < orderedLeaves.size();
    }

    @Override
    public AcademicContent next() {
        AcademicContent content = orderedLeaves.get(cursor);
        cursor++;
        return content;
    }

    public AcademicContent findByTitleOrFirst(String title) {
        if (title == null || title.isBlank()) {
            return first();
        }
        for (AcademicContent content : orderedLeaves) {
            if (title.equalsIgnoreCase(content.getTitle())) {
                return content;
            }
        }
        return first();
    }

    public AcademicContent findNextAfter(AcademicContent current) {
        if (current == null) {
            return first();
        }

        for (int i = 0; i < orderedLeaves.size(); i++) {
            AcademicContent content = orderedLeaves.get(i);
            if (content == current) {
                return i + 1 < orderedLeaves.size() ? orderedLeaves.get(i + 1) : null;
            }
        }
        return first();
    }

    public AcademicContent first() {
        return orderedLeaves.isEmpty() ? null : orderedLeaves.get(0);
    }

    private List<AcademicContent> flattenLeaves(AcademicContent root) {
        List<AcademicContent> leaves = new ArrayList<>();
        if (root == null) {
            return leaves;
        }

        Deque<AcademicContent> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            AcademicContent current = stack.pop();
            List<AcademicContent> children = current.getChildren();
            if (children == null || children.isEmpty()) {
                leaves.add(current);
                continue;
            }

            for (int i = children.size() - 1; i >= 0; i--) {
                AcademicContent child = children.get(i);
                if (child != null) {
                    stack.push(child);
                }
            }
        }

        return leaves;
    }
}

