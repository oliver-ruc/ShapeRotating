import java.util.function.Function;

import javax.swing.text.html.parser.Entity;

import Jama.Matrix;

public class DisplacementFunction {
    long _startTime;
    long _previousTime;
    long _endTime = Long.MAX_VALUE;
    Function<Long, Matrix> _displacementFunction;
    boolean _relativeTime = false;

    public DisplacementFunction(Function<Long, Matrix> displacementFunction, long startTime) {
        _displacementFunction = displacementFunction;
        _startTime = startTime;
        _previousTime = _startTime;
    }
    public DisplacementFunction(Function<Long, Matrix> displacementFunction, long startTime, boolean relativeTime) {
        this(displacementFunction, startTime);
        _relativeTime = relativeTime;
    }
    public DisplacementFunction(Function<Long, Matrix> displacementFunction, long startTime, long endTime) {
        this(displacementFunction, startTime);
        _endTime = endTime;
    }
    public DisplacementFunction(Function<Long, Matrix> displacementFunction, long startTime, long endTime, boolean relativeTime) {
        this(displacementFunction, startTime, endTime);
        _relativeTime = relativeTime;
    }
    

    public Matrix displacement(long currentTime) {
        if (currentTime < _startTime) {
            currentTime = _startTime;
        } else if (currentTime > _endTime) {
            currentTime = _endTime;
        }
        _previousTime = currentTime;
        return _relativeTime ? _displacementFunction.apply(currentTime-_startTime) : _displacementFunction.apply(currentTime);
    }

    public Matrix displacementNoUpdate(long currentTime) {
        if (currentTime < _startTime) {
            currentTime = _startTime;
        } else if (currentTime > _endTime) {
            currentTime = _endTime;
        }
        return _relativeTime ? _displacementFunction.apply(currentTime-_startTime) : _displacementFunction.apply(currentTime);
    }
    
    public Matrix displacementSinceLastCheck(long currentTime) {
        long _savedPreviousTime = _previousTime;
        _previousTime = currentTime;
        return this.displacementNoUpdate(currentTime).minus(this.displacementNoUpdate(_savedPreviousTime));
    }
}
