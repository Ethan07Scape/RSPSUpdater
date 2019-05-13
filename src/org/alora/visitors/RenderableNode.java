package org.alora.visitors;


import org.ethan.analyze.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;

public class RenderableNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return isValidClass(cn);
    }

    @Override
    public void visit() {

    }

    private boolean isValidClass(ClassNode cn) {
        if (cn.ownerless()) {
            if (cn.access == 1056) {
                if (cn.fieldCount() == 0) {
                    return cn.methods.size() > 10 && cn.fields.size() > 5;
                }
            }
        }
        return false;
    }
}