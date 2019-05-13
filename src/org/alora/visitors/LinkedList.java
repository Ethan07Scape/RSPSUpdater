package org.alora.visitors;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;


@VisitorInfo(hooks = {"sentinel", "tail"})
public class LinkedList extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount() == 2 && cn.fieldCount(desc("Node")) == 2 && cn.interfaces.size() == 1;
    }

    @Override
    public void visit() {
        visit(new Sentinel());
        for (FieldNode fn : getCn().fields) {
            if ((fn.access & ACC_STATIC) == 0 && !fn.name.equals(getFieldName("sentinel"))) {
                add("tail", fn);
                break;
            }
        }
    }

    private class Sentinel extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.owner.name.equals("<init>")) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() == PUTFIELD) {
                            AbstractNode neww = fmn.layer(INVOKESPECIAL, DUP, NEW);
                            if (neww != null) {
                                add(new FieldHookFrame("sentinel", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }
    }
}
