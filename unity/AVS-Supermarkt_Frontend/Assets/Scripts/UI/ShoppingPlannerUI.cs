using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using Random = UnityEngine.Random;

public class ShoppingPlannerUI : MonoBehaviour {

    [Header("UI Stuff")]
    public ItemButton itemPrefab;
    public RectTransform shopViewPortContent;
    public RectTransform listViewPortContent;

    [Header("Transform Parents")]
    public Transform shelfParent;
    public Cashdesk entrypoint;
    public List<Cashdesk> checkouts;

    [Header("Predefined Lists")]
    public List<Shelf> preListSmall;
    public List<Shelf> preListLarge;

    private List<ShopAsset> shoppingList;
    private ItemButton entrypointBtn;
    private ItemButton checkoutBtn;


    private void Awake() {
        var shelfs = new List<Shelf>(shelfParent.GetComponentsInChildren<Shelf>());
        shelfs.Sort((x, y) => x.productName.CompareTo(y.productName)); //Sort list alphabetical

        //For every shelf add a button in the list planner
        foreach(var shelf in shelfs) {
            var item = Instantiate(itemPrefab);
            item.transform.SetParent(shopViewPortContent);
            item.Setup(this, shelf, item.transform.GetSiblingIndex());
        }

        shoppingList = new List<ShopAsset>();


        //Add the entrypoint and checkout to the shopping list as not clickable buttons
        entrypointBtn = Instantiate(itemPrefab);
        entrypointBtn.transform.SetParent(listViewPortContent);
        entrypointBtn.Setup(this, GetEntrypoint(), -1);
        entrypointBtn.buttonComponent.interactable = false;

        checkoutBtn = Instantiate(itemPrefab);
        checkoutBtn.transform.SetParent(listViewPortContent);
        checkoutBtn.Setup(this, ChooseRandomCheckout(), -1);
        checkoutBtn.buttonComponent.interactable = false; ;
    }

    private void OnEnable() {
        ResetShopList();
    }

    private void ChangeCheckout(int index) {
        checkoutBtn.Setup(this, checkouts[index], -1);
    }

    public List<ShopAsset> GetShoppingList() {
        var list = new List<ShopAsset>(shoppingList);

        //Add the entrypoint and checkout to the list
        //The buttons are only visable in the list but not added to the internal list
        //This way its easier to handle because otherwise every time when a button gets added
        //to the shopping list, the checkout has to be move to the end of the list.
        list.Insert(0, entrypointBtn.ShopAsset);
        list.Add(checkoutBtn.ShopAsset);

        return list;
    }

    public Cashdesk GetEntrypoint() {
        return entrypoint;
    }

    public Cashdesk GetCheckout() {
        return checkoutBtn.ShopAsset as Cashdesk;
    }

    private Cashdesk ChooseRandomCheckout() {
        return checkouts[Random.Range(0, checkouts.Count)];
    }


    public void TryTransferItemToOtherList(ItemButton itmBtn) {
        if(itmBtn.transform.parent.Equals(shopViewPortContent)) {
            //Debug.Log("Move to shopping list.");
            MoveToShoppingList(itmBtn);

        } else if(itmBtn.transform.parent.Equals(listViewPortContent)) {
            //Debug.Log("Move out of shopping list.");
            MoveOutOfShoppingList(itmBtn);

        } else {
            throw new InvalidOperationException();
        }
    }

    public void MoveToShoppingList(ItemButton itmBtn) {
        itmBtn.transform.SetParent(listViewPortContent);
        shoppingList.Add(itmBtn.ShopAsset);

        //Set the checkout as the last elements
        checkoutBtn.transform.SetAsLastSibling();
    }

    public void MoveOutOfShoppingList(ItemButton itmBtn) {
        if(!shoppingList.Contains(itmBtn.ShopAsset)) return;

        itmBtn.transform.SetParent(shopViewPortContent);
        itmBtn.transform.SetSiblingIndex(itmBtn.SiblingIndex);
        shoppingList.Remove(itmBtn.ShopAsset);
    }


    public void ResetShopList() {
        foreach(var btn in listViewPortContent.GetComponentsInChildren<ItemButton>()) {
            btn.ResetButtonToShopList();
        }
    }

    public void LoadSmallPredefinedList() {
        LoadPredefinedList(preListSmall);
    }

    public void LoadLargePredefinedList() {
        LoadPredefinedList(preListLarge);
    }

    private void LoadPredefinedList(List<Shelf> list) {
        SetCashdesk(4);

        if(list == null || list.Count <= 0) {
            Debug.LogWarning("Given predefined list is null or empty.");
            return;
        }

        ResetShopList();
        var btns = new List<ItemButton>(shopViewPortContent.GetComponentsInChildren<ItemButton>());
        foreach(var shelf in list) {
            MoveToShoppingList(btns.Find(x => x.ShopAsset == shelf));
        }
    }


    public void SetCashdesk(int deskNumber) {
        ChangeCheckout(deskNumber - 1);
    }
}
