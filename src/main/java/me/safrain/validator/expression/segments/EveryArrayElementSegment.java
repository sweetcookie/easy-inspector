
package me.safrain.validator.expression.segments;

import me.safrain.validator.expression.SegmentContext;

public class EveryArrayElementSegment implements PathSegment {


    @Override
    public boolean process(final Object object, int index, final SegmentContext context, boolean optional) {
        if (!context.getArrayAccessor().acceptType(object)) {
            return context.onRejected(object);
        }
        int size = context.getArrayAccessor().size(object);

        context.activateSuppressMode(this, false);
        try {
            boolean last = context.isLastSegment(index);
            for (int i = 0; i < size; i++) {
                Object o = context.getArrayAccessor().accessIndex(object, i);
                if (!(last ?
                        context.onValidation(o) :
                        context.getSegment(index + 1).process(o, index + 1, context, optional))) {
                    return false;
                }
            }
        } finally {
            context.deactivateSuppressMode(this);
        }
        return true;
    }
}