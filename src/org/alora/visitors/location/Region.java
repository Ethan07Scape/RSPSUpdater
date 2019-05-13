package org.alora.visitors.location;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;


@VisitorInfo(hooks = {"tiles", "objects"})
public class Region extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        FieldNode f = getFirstField(cn, fieldNode -> fieldNode.desc.equals("[[[L" + clazz("Tile") + ";"));
        if (f != null) {
            if (cn.getAbnormalFieldCount() > 0) {
                return true;
            }
        }
        return cn.fieldCount("[[[L" + desc("Tile")) >= 1;
    }

    @Override
    public void visit() {
        FieldNode tiles = getFirstField(getCn(), fieldNode -> fieldNode.desc.equals("[[[L" + clazz("Tile") + ";"));
        if (tiles != null) {
            add(new FieldHookFrame("tiles", tiles));
        }
        FieldNode objects = getFirstField(getCn(), fieldNode -> fieldNode.desc.equals("[" + desc("InteractableObject")));
        if (objects != null) {
            add(new FieldHookFrame("tiles", objects));
        }

    }
}