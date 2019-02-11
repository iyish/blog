swfobject.addDomLoadEvent(function () {
    var webcamAvailable = false;
    var currentTab = 'upload';
    const sourcePicUrl = '';
    const callback = function (msg) {
        const id = this.id;
        switch (msg.code) {
            case 2:
                //如果加载原图成功，说明进入了编辑面板，显示保存和取消按钮，隐藏拍照按钮
                if (msg.type === 0) {
                    if (id === "swf1") {
                        $('#webcamPanelButton').hide();
                        $('#editorPanelButtons').show();
                    }
                }
                //否则会转到上传面板
                else {
                    //隐藏所有按钮
                    if (id === "swf1") $('#editorPanelButtons,#webcamPanelButton').hide();
                }
                break;
            case 3:
                //如果摄像头已准备就绪且用户已允许使用，显示拍照按钮。
                if (msg.type === 0) {
                    if (id === "swf1") {
                        $('.button_shutter').removeClass('Disabled');
                        $('#webcamPanelButton').show();
                        webcamAvailable = true;
                    }
                }
                else {
                    if (id === "swf1") {
                        webcamAvailable = false;
                        $('#webcamPanelButton').hide();
                    }
                    //如果摄像头已准备就绪但用户已拒绝使用。
                    if (msg.type === 1) {
                        layer.alert('用户拒绝使用摄像头!');
                    }
                    //如果摄像头已准备就绪但摄像头被占用。
                    else {
                        layer.alert('摄像头被占用!');
                    }
                }
                break;
            case 4:
                layer.alert("您选择的原图片文件大小（" + msg.content + "）超出了指定的值(2MB)。");
                break;
            case 5:
                //如果上传成功
                if (msg.type === 0) {
                    const time = $.now();
                    const html = "<div style='margin-top:30px;text-align:center;vertical-align: middle;overflow:auto'><img style='display:block;margin:0 auto;' src='sys/head/img?t=" + time + "'/></div>";
                    window.parent.sopen(html, '预览新头像', 300, 250, yesCb);
                }
                break;
        }
    };
    const yesCb = function (index) {
        layer.close(index);
        window.parent.location.reload();//刷新父页面
    };
    const swf1 = new fullAvatarEditor('swf1', 335, {
        id: 'swf1',
        upload_url: 'sys/head/upload',
        // src_url: sourcePicUrl,			    //默认加载的原图片的url
        tab_visible: false,				    //不显示选项卡，外部自定义
        button_visible: false,				//不显示按钮，外部自定义
        src_upload: 2,						//是否上传原图片的选项：2-显示复选框由用户选择，0-不上传，1-上传
        checkbox_visible: false,			//不显示复选框，外部自定义
        browse_box_align: 'left',			//图片选择框的水平对齐方式。left：左对齐；center：居中对齐；right：右对齐；数值：相对于舞台的x坐标
        webcam_box_align: 'left',			//摄像头拍照框的水平对齐方式，如上。
        avatar_sizes: '80*80',			//定义单个头像
        avatar_sizes_desc: '80*80像素',	//头像尺寸的提示文本
        avatar_intro: '      最终尺寸头像', //头像简介
        avatar_tools_visible: true,			//是否显示颜色调整工具
        isShowUploadResultIcon: false,
        src_size: "2MB",                    //选择的本地图片文件所允许的最大值，必须带单位，如888Byte，88KB，8MB
        src_size_over_limit: "文件大小超出2MB，请重新选择图片。" //当选择原图片文件大小超出指定最大值时提示文本,可使用占位符{0}表示选择原图片文件大小。
    }, callback);
    //Tab页点击事件
    $('#avatar-tab li').click(function () {
        if (currentTab != this.id) {
            currentTab = this.id;
            $(this).addClass('active');
            $(this).siblings().removeClass('active');
            //如果是点击“相册选取”
            if (this.id === 'albums') {
                //隐藏flash
                hideSWF();
                showAlbums();
            }
            else {
                hideAlbums();
                showSWF();
                if (this.id === 'webcam') {
                    $('#editorPanelButtons').hide();
                    if (webcamAvailable) {
                        $('.button_shutter').removeClass('Disabled');
                        $('#webcamPanelButton').show();
                    }
                }
                else {
                    //隐藏所有按钮
                    $('#editorPanelButtons,#webcamPanelButton').hide();
                }
            }
            swf1.call('changepanel', this.id);
        }
    });
    //复选框事件
    $('#src_upload').change(function () {
        swf1.call('srcUpload', this.checked);
    });
    //点击上传按钮的事件
    $('.button_upload').click(function () {
        swf1.call('upload');
    });
    //点击取消按钮的事件
    $('.button_cancel').click(function () {
        var activedTab = $('#avatar-tab li.active')[0].id;
        if (activedTab === 'albums') {
            hideSWF();
            showAlbums();
        }
        else {
            swf1.call('changepanel', activedTab);
            if (activedTab === 'webcam') {
                $('#editorPanelButtons').hide();
                if (webcamAvailable) {
                    $('.button_shutter').removeClass('Disabled');
                    $('#webcamPanelButton').show();
                }
            }
            else {
                //隐藏所有按钮
                $('#editorPanelButtons,#webcamPanelButton').hide();
            }
        }
    });
    //点击拍照按钮的事件
    $('.button_shutter').click(function () {
        if (!$(this).hasClass('Disabled')) {
            $(this).addClass('Disabled');
            swf1.call('pressShutter');
        }
    });
    //从相册中选取
    $('#userAlbums a').click(function () {
        var sourcePic = this.href;
        swf1.call('loadPic', sourcePic);
        //隐藏相册
        hideAlbums();
        //显示flash
        showSWF();
        return false;
    });

    //隐藏flash
    function hideSWF() {
        //将宽高设置为0的方式来隐藏flash，而不能使用将其display样式设置为none的方式来隐藏，否则flash将不会被加载，隐藏时储存其宽高，以便后期恢复
        $('#flash1').data({
            w: $('#flash1').width(),
            h: $('#flash1').height()
        }).css({
            width: '0px',
            height: '0px',
            overflow: 'hidden'
        });
        //隐藏所有按钮
        $('#editorPanelButtons,#webcamPanelButton').hide();
    }

    //显示flash
    function showSWF() {
        $('#flash1').css({
            width: $('#flash1').data('w'),
            height: $('#flash1').data('h')
        });
    }

    //显示相册
    function showAlbums() {
        $('#userAlbums').show();
    }

    //隐藏相册
    function hideAlbums() {
        $('#userAlbums').hide();
    }
});
