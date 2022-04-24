import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.text.html.parser.Entity;

import Jama.Matrix;

public class TrollingFunction {
    long _startTime = 0;
    long _previousTime;
    long _endTime = Long.MAX_VALUE;
    BiFunction<Long, Solid, DisplacementAndRotation> _displacementAndRotationFunction;
    boolean _relativeTime = false;

    public TrollingFunction(BiFunction<Long, Solid, DisplacementAndRotation> displacementAndRotationFunction, long startTime) {
        _displacementAndRotationFunction = displacementAndRotationFunction;
        _startTime = startTime;
        _previousTime = _startTime;
    }
    public TrollingFunction(BiFunction<Long, Solid, DisplacementAndRotation> displacementAndRotationFunction, long startTime, boolean relativeTime) {
        this(displacementAndRotationFunction, startTime);
        _relativeTime = relativeTime;
    }
    public TrollingFunction(BiFunction<Long, Solid, DisplacementAndRotation> displacementAndRotationFunction, long startTime, long endTime) {
        this(displacementAndRotationFunction, startTime);
        _endTime = endTime;
    }
    public TrollingFunction(BiFunction<Long, Solid, DisplacementAndRotation> displacementAndRotationFunction, long startTime, long endTime, boolean relativeTime) {
        this(displacementAndRotationFunction, startTime, endTime);
        _relativeTime = relativeTime;
    }

    public DisplacementAndRotation displacementAndRotationWithUpdate(long currentTime, Solid solid) {
        if (currentTime < _startTime) {
            currentTime = _startTime;
        } else if (currentTime > _endTime) {
            currentTime = _endTime;
        }
        _previousTime = currentTime;
        return _relativeTime ? _displacementAndRotationFunction.apply(currentTime-_startTime, solid) : _displacementAndRotationFunction.apply(currentTime, solid);
    }

    public DisplacementAndRotation displacementAndRotationWithoutUpdate(long currentTime, Solid solid) {
        if (currentTime < _startTime) {
            currentTime = _startTime;
        } else if (currentTime > _endTime) {
            currentTime = _endTime;
        }
        return _relativeTime ? _displacementAndRotationFunction.apply(currentTime-_startTime, solid) : _displacementAndRotationFunction.apply(currentTime, solid);
    }
    
    public DisplacementAndRotation displacementAndRotationSinceLastCheck(long currentTime, Solid solid) {
        long _savedPreviousTime = _previousTime;
        _previousTime = currentTime;
        // return this.displacementAndRotationWithoutUpdate(currentTime, solid).minus(this.displacementAndRotationWithoutUpdate(_savedPreviousTime, solid));
        return this.displacementAndRotationWithoutUpdate(currentTime, solid).minus(DisplacementAndRotation.displacement(solid.getCenter()));
    }
}
