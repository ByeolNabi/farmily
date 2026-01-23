using UnityEngine;

[ExecuteAlways]
public class BackgroundFit : MonoBehaviour
{
    [SerializeField] private SpriteRenderer sr;
    [SerializeField] private bool fill = true; 

    void OnEnable() => Fit();
    void Start() => Fit();
    void OnValidate() => Fit();

    [ContextMenu("Fit Now")]
    public void Fit()
    {
        if(!sr) sr = GetComponent<SpriteRenderer>();
        if(!sr || !sr.sprite) return;

        Camera cam = Camera.main;
        if(!cam || !cam.orthographic) return;

        float worldScreenHeight = cam.orthographicSize * 2f;
        float worldScreenWidth = worldScreenHeight * cam.aspect;

        Vector2 spriteSize = sr.sprite.bounds.size;

        float scaleX = worldScreenWidth / spriteSize.x;
        float scaleY = worldScreenHeight / spriteSize.y;

        float final = fill ? Mathf.Max(scaleX, scaleY) : Mathf.Min(scaleX, scaleY);
        transform.localScale = new Vector3(final, final, 1f);

        // 카메라 정면에 오도록
        transform.position = new Vector3(cam.transform.position.x, cam.transform.position.y, 0f);
    }   
}
