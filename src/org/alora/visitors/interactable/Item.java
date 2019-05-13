package org.alora.visitors.interactable;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"id", "stackSize"})
public class Item extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.superName.equals(clazz("RenderableNode"))) {
            if (cn.getFieldTypeCount() == 2) {
                if (cn.access == 48) {
                    return cn.fieldCount("I") > 4;
                }
            }
        }
        return false;
    }

    @Override
    public void visit() {
        visit(new InfoHooks());
    }

    private class InfoHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKEVIRTUAL && mmn.desc().startsWith("(I")) {
                        FieldMemberNode id = (FieldMemberNode) mmn.layer(INVOKESTATIC, GETFIELD);
                        if (id != null && id.owner().equals(getCn().name) && id.desc().equals("I")) {
                            FieldMemberNode stack = (FieldMemberNode) mmn.layer(GETFIELD);
                            if (stack != null && stack.desc().equals("I")) {
                                add(new FieldHookFrame("id", id.fin()));
                                add(new FieldHookFrame("stacksize", stack.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}