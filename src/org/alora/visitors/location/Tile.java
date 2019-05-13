package org.alora.visitors.location;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"objects", "boundary"})
public class Tile extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("Z") == 3;
    }

    @Override
    public void visit() {
        add("objects", getFirstField(getCn(), fieldNode -> fieldNode.desc.equals("[" + desc("InteractableObject"))));
        add("boundary", getFirstField(getCn(), fieldNode -> fieldNode.desc.equals(desc("Boundary"))));
    }
}