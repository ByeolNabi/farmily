using UnityEngine;
using System;

public class CharacterFsmController : MonoBehaviour
{
    [SerializeField] private Animator animator;
    [SerializeField] private RectTransform rectTransform;
    [SerializeField] private RuntimeAnimatorController positiveIdleController;
    [SerializeField] private RuntimeAnimatorController drinkController;
    [SerializeField] private AnimationClip drinkClip;
    [SerializeField] private string positiveIdleStateName = "IDLE";
    [SerializeField] private string drinkStateName = "Drink";
    [SerializeField] private Vector2 positiveIdlePosition;
    [SerializeField] private Vector3 positiveIdleScale = Vector3.one;
    [SerializeField] private Vector2 drinkPosition;
    [SerializeField] private Vector3 drinkScale = Vector3.one;
    private string currentState = "NONE";
    private IState activeState;
    private PositiveIdleState positiveIdleState;
    private DrinkState drinkState;

    private void Awake()
    {
        positiveIdleState = new PositiveIdleState(this);
        drinkState = new DrinkState(this);
    }

    private void Start()
    {
        if (activeState == null)
        {
            TransitionTo(positiveIdleState);
        }
    }

    private void Update()
    {
        activeState?.Tick(Time.deltaTime);
    }

    private void OnEnable()
    {
        mqttManagerJS.StateChanged += OnStateChanged;
        mqttManagerJS.DeviceEventReceived += OnDeviceEventReceived;
    }

    private void OnDisable()
    {
        mqttManagerJS.StateChanged -= OnStateChanged;
        mqttManagerJS.DeviceEventReceived -= OnDeviceEventReceived;
    }

    private void OnStateChanged(string state)
    {
        if (string.IsNullOrEmpty(state))
        {
            return;
        }

        if (string.Equals(state, currentState, StringComparison.OrdinalIgnoreCase))
        {
            return;
        }

        currentState = state;
        Debug.Log($"CharacterFsmController: state -> {currentState}");
    }

    private void OnDeviceEventReceived(string eventName)
    {
        if (string.IsNullOrEmpty(eventName))
        {
            return;
        }

        if (!string.Equals(eventName, "WATER_DETECTED", StringComparison.OrdinalIgnoreCase))
        {
            return;
        }

        TransitionTo(drinkState);
    }

    private void TransitionTo(IState nextState)
    {
        if (nextState == null)
        {
            return;
        }

        if (activeState == nextState)
        {
            return;
        }

        activeState?.Exit();
        activeState = nextState;
        activeState.Enter();
    }

    private interface IState
    {
        void Enter();
        void Tick(float deltaTime);
        void Exit();
    }

    private sealed class PositiveIdleState : IState
    {
        private readonly CharacterFsmController owner;

        public PositiveIdleState(CharacterFsmController owner)
        {
            this.owner = owner;
        }

        public void Enter()
        {
            if (!owner.AreAnimatorRefsReady())
            {
                return;
            }

            owner.ApplyTransformForState(owner.positiveIdlePosition, owner.positiveIdleScale);
            owner.animator.runtimeAnimatorController = owner.positiveIdleController;
            owner.animator.Play(owner.positiveIdleStateName, 0, 0f);
        }

        public void Tick(float deltaTime)
        {
        }

        public void Exit()
        {
        }
    }

    private sealed class DrinkState : IState
    {
        private readonly CharacterFsmController owner;
        private float elapsed;
        private float duration;

        public DrinkState(CharacterFsmController owner)
        {
            this.owner = owner;
        }

        public void Enter()
        {
            if (!owner.AreAnimatorRefsReady())
            {
                return;
            }

            elapsed = 0f;
            duration = owner.drinkClip != null ? owner.drinkClip.length : 1f;

            owner.ApplyTransformForState(owner.drinkPosition, owner.drinkScale);
            owner.animator.runtimeAnimatorController = owner.drinkController;
            owner.animator.Play(owner.drinkStateName, 0, 0f);
        }

        public void Tick(float deltaTime)
        {
            if (duration <= 0f)
            {
                duration = 1f;
            }

            elapsed += deltaTime;
            if (elapsed >= duration)
            {
                owner.TransitionTo(owner.positiveIdleState);
            }
        }

        public void Exit()
        {
        }
    }

    private bool AreAnimatorRefsReady()
    {
        if (animator == null || drinkController == null || positiveIdleController == null)
        {
            Debug.LogWarning("CharacterFsmController: missing animator or controllers.");
            return false;
        }

        return true;
    }

    private void ApplyTransformForState(Vector2 position, Vector3 scale)
    {
        if (rectTransform != null)
        {
            rectTransform.anchoredPosition = position;
            rectTransform.localScale = scale;
            return;
        }

        transform.localPosition = new Vector3(position.x, position.y, transform.localPosition.z);
        transform.localScale = scale;
    }
}
