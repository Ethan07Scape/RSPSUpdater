package org.alora.visitors;

import org.ethan.analyze.visitor.GraphVisitor;
import org.ethan.analyze.visitor.VisitorInfo;
import org.ethan.hooks.FieldHookFrame;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

@VisitorInfo(hooks = {"buckets", "tail", "head"})
public class HashTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        String desc = desc("Node");
        return cn.ownerless() && cn.fieldCount(desc) == 2 && cn.fieldCount("[" + desc) == 1;
    }

    @Override
    public void visit() {
        add("buckets", getCn().getField(null, "[" + desc("Node")));

        visit(new NodeHooks());
    }

    private class NodeHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ACMPEQ && jn.first(AALOAD) != null) {
                        FieldMemberNode tail = jn.firstField();
                        String node = desc("Node");
                        if (tail != null && tail.owner().equals(getCn().name) && tail.desc().equals(node)) {
                            add(new FieldHookFrame("tail", tail.fin()));
                            for (FieldNode fn : getCn().fields) {
                                if (fn.desc.equals(node)) {
                                    if (!fn.name.equals(tail.name())) {
                                        add(new FieldHookFrame("head", fn));
                                        break;
                                    }
                                }
                            }
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

}