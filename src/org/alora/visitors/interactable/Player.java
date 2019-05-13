package org.alora.visitors.interactable;


import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

@VisitorInfo(hooks = {"name"})
public class Player extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Character"))
                && cn.fieldCount("Z") >= 1;
    }

    @Override
    public void visit() {
        add("actions", getCn().getField(null, "[Ljava/lang/String;"));
        for (FieldNode fn : getCn().fields) {
            if (fn.desc.equals("Ljava/lang/String;")) {
                if (fn.access == 0) {
                    add("name", fn);
                    break;
                }
            }
        }
    }
}