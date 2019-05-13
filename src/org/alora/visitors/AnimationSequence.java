package org.alora.visitors;


import org.ethan.analyze.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;

public class AnimationSequence extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("CacheableNode")) && cn.getFieldTypeCount() == 3 && cn.fieldCount("Z") == 1;
    }

    @Override
    public void visit() {
    }
}