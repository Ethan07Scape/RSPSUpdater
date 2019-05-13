package org.alora.visitors.nodes;

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

@VisitorInfo(hooks = {"previous", "next"})
public class CacheableNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("L" + cn.name + ";") == 2;
    }


    @Override
    public void visit() {
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
                    if (jn.opcode() == IFNONNULL) {
                        FieldMemberNode fmn = jn.firstField();
                        if (fmn != null && fmn.desc().equals("L" + getCn().name + ";")) {
                            add(new FieldHookFrame("previous", fmn.fin()));
                            for (FieldNode fn : getCn().fields) {
                                if (fn.desc.equals("L" + getCn().name + ";")) {
                                    if (!fn.name.equals(fmn.name())) {
                                        add(new FieldHookFrame("next", fmn.fin()));
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