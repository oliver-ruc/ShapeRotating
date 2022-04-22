import java.util.function.Function;

import Jama.Matrix;

public class DisplacementFunction {
    long _startTime;
    long _previousTime;
    long _endTime = Long.MAX_VALUE;
    Function<Long, Matrix> _displacementFunction;
    public DisplacementFunction(Function<Long, Matrix> displacementFunction, long startTime) {
        _displacementFunction = displacementFunction;
        _startTime = startTime;
        _previousTime = _startTime;
    }
    public DisplacementFunction(Function<Long, Matrix> displacementFunction, long startTime, long endTime) {
        this(displacementFunction, startTime);
        _endTime = endTime;
    }
    public Matrix displacement(long currentTime) {
        if (currentTime < _startTime) {
            currentTime = _startTime;
        } else if (currentTime > _endTime) {
            currentTime = _endTime;
        }
        _previousTime = currentTime;
        return _displacementFunction.apply(currentTime);
    }
    public Matrix displacementNoUpdate(long currentTime) {
        if (currentTime < _startTime) {
            currentTime = _startTime;
        } else if (currentTime > _endTime) {
            currentTime = _endTime;
        }
        return _displacementFunction.apply(currentTime);
    }
    public Matrix displacementSinceLastCheck(long currentTime) {
        Matrix output = this.displacementNoUpdate(currentTime).minus(this.displacementNoUpdate(_previousTime));
        _previousTime = currentTime;
        return output;
    }
}
