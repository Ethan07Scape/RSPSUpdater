package org.alora.visitors.interactable;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"id"})
public class InteractableObject extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.getAbnormalFieldCount() == 1 && cn.getFieldTypeCount() == 3 &&
                cn.fieldCount("I") >= 9 && cn.fieldCount(desc("RenderableNode")) == 1;
    }

    @Override
    public void visit() {
        add("id", getFirstField(getCn(), fieldNode -> fieldNode.desc.equals("J")));
    }

}