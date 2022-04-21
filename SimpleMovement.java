import java.util.function.BiFunction;
import java.util.function.Supplier;

import Jama.Matrix;

public class SimpleMovement {
    long _startTime;
    long _previousTime;
    long _endTime = Long.MAX_VALUE;
    Supplier<Matrix> _defaultPosition;
    BiFunction<Long, Long, Matrix> _movementFunction;
    public SimpleMovement(Supplier<Matrix> defaultPosition, BiFunction<Long, Long, Matrix> movementFunction, long startTime) {
        _defaultPosition = defaultPosition;
        _movementFunction = movementFunction;
        _startTime = startTime;
        _previousTime = _startTime;
    }
    public SimpleMovement(Supplier<Matrix> defaultPosition, BiFunction<Long, Long, Matrix> movementFunction, long startTime, long endTime) {
        this(defaultPosition, movementFunction, startTime);
        _endTime = endTime;
    }
    public Matrix movement(long currentTime) {
        if (currentTime < _startTime || currentTime > _endTime) {
            return _defaultPosition.get();
        } else {
            long deltaTime = currentTime - _previousTime;
            _previousTime = currentTime;
            return _movementFunction.apply(currentTime, deltaTime);
        }
    } 
}
