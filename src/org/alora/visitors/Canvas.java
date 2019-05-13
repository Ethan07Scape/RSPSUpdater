package org.alora.visitors;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"dummyHook"})
public class Canvas extends GraphVisitor {


    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals("java/awt/Canvas") && cn.fieldCount("Ljava/awt/Component;") == 1;
    }

    @Override
    public void visit() {
        add("dummyHook", getCn().fields.get(1));
    }
}