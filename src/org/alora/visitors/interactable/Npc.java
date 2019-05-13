package org.alora.visitors.interactable;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"definition"})
public class Npc extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.superName.equals(clazz("Character"))) {
            if (!cn.name.equals(clazz("Player"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visit() {
        add("definition", getCn().getField(null, desc("NpcDefinition")));
    }
}