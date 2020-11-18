$(document).ready(function (){

    var dataCategory = {};

    $("#new-category").on("click", function (){
        dataCategory = {};
        $("#input-category-name").val("");
        $("#input-category-desc").val("");
    })

    $(".edit-category").on("click", function () {
        var cateInfo = $(this).data("category")
        console.log(cateInfo);
        NProgress.start();
        axios.get("/api/category/detail/" + cateInfo).then(function(res){
            NProgress.done();
            if(res.data.success){
                dataCategory.id = res.data.data.id;
                $("#input-category-name").val(res.data.data.name);
                $("#input-category-desc").val(res.data.data.short_desc);
            }
            else {
                console.log("saihuhu")
            }
        },function (err){
            NProgress.done();
        })
    });

    $(".btn-save-category").on("click", function (e){
        if($("#input-category-name").val() === "" || $("#input-category-desc").val() === ""){
            swal(
                'Error',
                'You need to fill all value',
                'error'
            );
            return;
        }

        dataCategory.name = $("#input-category-name").val();
        dataCategory.short_desc = $("#input-category-desc").val();
        NProgress.start();
        var linkPost = "/api/category/create/";
        if(dataCategory.id) {
            linkPost = "/api/category/update/" + dataCategory.id;
        }
        axios.post(linkPost, dataCategory).then(function(res){
            NProgress.done();
            if(res.data.success) {
                swal(
                    'Good job!',
                    res.data.message,
                    'success'
                ).then(function() {
                    location.reload();
                });
            } else {
                swal(
                    'Error',
                    res.data.message,
                    'error'
                );
            }
        }, function(err){
            NProgress.done();
            swal(
                'Error',
                'Some error when saving category',
                'error'
            );
        })
    });
});
