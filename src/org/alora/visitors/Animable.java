package org.alora.visitors;

import org.ethan.analyze.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class Animable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode c) {
        if (c.superName.equals(clazz("RenderableNode"))) {
            for (int I = 0; I < c.methods.size(); ++I) {
                MethodNode method = c.methods.get(I);
                if (method.desc.equals("(IIIIIIIZL" + clazz("RenderableNode") + ";)V"))
                    return true;
            }
        }
        return false;
    }

    @Override
    public void visit() {
    }
}