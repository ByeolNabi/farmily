using UnityEngine;

public enum CharacterState
{
    Idle = 0,
    //Happy = 1,
    //Sad = 2,
    //Alert = 3,
    //Sleep = 4
}

public class CharacterFsmController : MonoBehaviour
{
    [SerializeField] private Animator animator;

    private CharacterState currentState = CharacterState.Idle;

    private void Awake()
    {
        if (animator == null) animator = GetComponent<Animator>();
    }

    private void Start()
    {
        SetState(CharacterState.Idle); 
    }

    public void OnSensorEvent(string eventType, float value)
    {
        //if (eventType == "temp" && value > 30) SetState(CharacterState.Alert);
        //else if (eventType == "light" && value < 0.2f) SetState(CharacterState.Sleep);
        //else SetState(CharacterState.Idle);

        SetState(CharacterState.Idle);
    }

    private void SetState(CharacterState next)
    {
        if (currentState == next) return;

        currentState = next;
        animator.SetInteger("state", (int)currentState);
    }
}
