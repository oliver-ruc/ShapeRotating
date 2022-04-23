import java.util.function.Function;

import Jama.Matrix;

public class RotationFunction {
    long _startTime;
    long _previousTime;
    long _endTime = Long.MAX_VALUE;
    Function<Long, Matrix> _rotationFunction;
    boolean _relativeTime = false;

    public RotationFunction(Function<Long, Matrix> rotationFunction, long startTime) {
        _rotationFunction = rotationFunction;
        _startTime = startTime;
        _previousTime = _startTime;
    }

    public RotationFunction(Function<Long, Matrix> rotationFunction, long startTime, boolean relativeTime) {
        this(rotationFunction, startTime);
        _relativeTime = relativeTime;
    }

    public RotationFunction(Function<Long, Matrix> rotationFunction, long startTime, long endTime) {
        this(rotationFunction, startTime);
        _endTime = endTime;
    }

    public RotationFunction(Function<Long, Matrix> rotationFunction, long startTime, long endTime, boolean relativeTime) {
        this(rotationFunction, startTime, endTime);
        _relativeTime = relativeTime;
    }
    public Matrix rotation(long currentTime) {
        if (currentTime < _startTime) {
            currentTime = _startTime;
        } else if (currentTime > _endTime) {
            currentTime = _endTime;
        }
        _previousTime = currentTime;
        return _relativeTime ? _rotationFunction.apply(currentTime-_startTime) : _rotationFunction.apply(currentTime);
    }
    public Matrix rotationNoUpdate(long currentTime) {
        if (currentTime < _startTime) {
            currentTime = _startTime;
        } else if (currentTime > _endTime) {
            currentTime = _endTime;
        }
        return _relativeTime ? _rotationFunction.apply(currentTime-_startTime) : _rotationFunction.apply(currentTime);
    }
    public Matrix rotationSinceLastCheck(long currentTime) {
        Matrix output = this.rotationNoUpdate(currentTime).times(this.rotationNoUpdate(_previousTime).inverse());
        _previousTime = currentTime;
        return output;
    }
    
}
